package com.object.ch04.movie.after.policy;

import com.object.ch04.domain.Money;
import com.object.ch04.movie.after.Movie;
import com.object.ch04.movie.after.condition.DiscountCondition;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.object.ch04.movie.after.MovieType.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DiscountPolicyFactoryTest {

    @Test
    void 금액할인정책의_해당하는_클래스를_로드한다() {
        final DiscountPolicyFactory discountPolicyFactory = new DiscountPolicyFactory();

        final DiscountPolicy discountPolicy = discountPolicyFactory.getPolicy(AMOUNT_DISCOUNT);

        assertThat(discountPolicy).isInstanceOf(AmountDiscountPolicy.class);
    }

    @Test
    void 금액할인정책은_영화요금에서_할인금액을_차감한다() {

        final DiscountCondition 순번조건 = new DiscountCondition(1);
        final Movie 아바타_금액할인 = new Movie("아바타", Duration.ofMinutes(90L), Money.wons(10000), Money.wons(1000), 순번조건);

        final DiscountPolicyFactory discountPolicyFactory = new DiscountPolicyFactory();

        final DiscountPolicy discountPolicy = discountPolicyFactory.getPolicy(아바타_금액할인.getMovieType());

        final Money discount = discountPolicy.discount(아바타_금액할인, LocalDateTime.now(), 1);

        assertAll(
                () -> assertThat(discountPolicy).isInstanceOf(AmountDiscountPolicy.class),
                () -> assertThat(discount).isEqualTo(Money.wons(9000))
        );
    }

    @Test
    void 비율할인정책은_영화요금에서_할인율을_계산하여_차감한다() {

        final DiscountCondition 순번조건 = new DiscountCondition(1);
        final Movie 아바타_비율할인 = new Movie("아바타", Duration.ofMinutes(90L), Money.wons(10000), 0.2, 순번조건);

        final DiscountPolicyFactory discountPolicyFactory = new DiscountPolicyFactory();

        final DiscountPolicy discountPolicy = discountPolicyFactory.getPolicy(아바타_비율할인.getMovieType());

        final Money discount = discountPolicy.discount(아바타_비율할인, LocalDateTime.now(), 1);

        assertAll(
                () -> assertThat(discountPolicy).isInstanceOf(PerCentDiscountPolicy.class),
                () -> assertThat(discount).isEqualTo(Money.wons(8000))
        );
    }

    @Test
    void 미적용정책은_영화요금_그대로_계산한다() {

        final Movie 아바타_미적용 = new Movie("아바타", Duration.ofMinutes(90L), Money.wons(10000));

        final DiscountPolicyFactory discountPolicyFactory = new DiscountPolicyFactory();

        final DiscountPolicy discountPolicy = discountPolicyFactory.getPolicy(아바타_미적용.getMovieType());

        final Money discount = discountPolicy.discount(아바타_미적용, LocalDateTime.now(), 1);

        assertAll(
                () -> assertThat(discountPolicy).isInstanceOf(NoneDiscountPolicy.class),
                () -> assertThat(discount).isEqualTo(Money.wons(10000))
        );
    }
}