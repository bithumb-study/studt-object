package com.object.ch05.step03;

import com.object.ch05.Money;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("step 03 - Screening 객체 테스트")
class ScreeningTest {

    @Test
    void 영화금액의_순번할인규칙의_금액할인을_적용하고_금액을_확인한다() {

        final SequenceCondition sequenceCondition = new SequenceCondition(1);
        final Movie 아바타 = new Movie("아바타", Duration.ofMinutes(120), Money.wons(10_000), Lists.newArrayList(sequenceCondition)
                , MovieType.AMOUNT_DISCOUNT, Money.wons(800), 0);
        final Screening screening = new Screening(아바타, 1, null);
        final Customer 손님 = new Customer("손님", "1");
        final Reservation reserve = screening.reserve(손님, 1);

        assertAll(
                () -> assertThat(reserve.getFee()).isEqualTo(Money.wons(9_200))
        );
    }

    @Test
    void 영화금액의_순번할인규칙의_비율할인을_적용하고_금액을_확인한다() {

        final SequenceCondition sequenceCondition = new SequenceCondition(1);
        final Movie 아바타 = new Movie("아바타", Duration.ofMinutes(120), Money.wons(10_000), Lists.newArrayList(sequenceCondition)
                , MovieType.PERCENT_DISCOUNT, null, 0.2);
        final Screening screening = new Screening(아바타, 1, null);
        final Customer 손님 = new Customer("손님", "1");
        final Reservation reserve = screening.reserve(손님, 1);

        assertAll(
                () -> assertThat(reserve.getFee()).isEqualTo(Money.wons(8_000))
        );
    }
}