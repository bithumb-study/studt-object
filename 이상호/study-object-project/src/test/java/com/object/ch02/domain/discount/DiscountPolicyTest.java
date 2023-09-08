package com.object.ch02.domain.discount;

import com.object.ch02.domain.Money;
import com.object.ch02.domain.Movie;
import com.object.ch02.domain.Screening;
import com.object.ch02.domain.discount.condition.SequenceCondition;
import com.object.ch02.domain.discount.policy.AmountDiscountPolicy;
import com.object.ch02.domain.discount.policy.NoneDiscountPolicy;
import com.object.ch02.domain.discount.policy.PercentDiscountPolicy;
import com.object.ch02.fixture.MovieFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("할인 정책 객체의 단위 테스트")
class DiscountPolicyTest {

    @Test
    void 영화_금액이_금액_할인_정책이_적용된_할인_금액을_구한다() {

        final long amount = 800;
        final Movie 아바타 = MovieFixture.createMovie("아바타", 120, 10_000, amount, 1);
        final Screening 상영 = new Screening(아바타, 1, null);

        final AmountDiscountPolicy 금액할인정책 = new AmountDiscountPolicy(Money.wons(amount), new SequenceCondition(1));
        final Money 요금 = 금액할인정책.calculateDiscountAmount(상영);

        assertThat(요금).isEqualTo(Money.wons(amount));
    }

    @Test
    void 영화_금액이_비율_할인_정책이_적용된_할인_금액을_구한다() {

        final long amount = 10_000;
        final double percent = 0.1;
        final Movie 아바타 = MovieFixture.createMovie("아바타", 120, amount, percent, 1);
        final Screening 상영 = new Screening(아바타, 1, null);

        final PercentDiscountPolicy 비율할인정책 = new PercentDiscountPolicy(percent, new SequenceCondition(1));
        final Money 요금 = 비율할인정책.calculateDiscountAmount(상영);

        assertThat(요금).isEqualTo(Money.wons(amount * (percent)));
    }

    @Test
    void 영화_금액이_할인_정책이_적용되어_있지_않다() {

        final Movie 아바타 = MovieFixture.createMovie("아바타", 120, 10_000);
        final Screening 상영 = new Screening(아바타, 1, null);

        final NoneDiscountPolicy 정책없음 = new NoneDiscountPolicy();
        final Money 요금 = 정책없음.calculateDiscountAmount(상영);

        assertThat(요금).isEqualTo(Money.wons(0));
    }
}