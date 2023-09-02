package com.object.ch02.domain;

import com.object.ch02.fixture.MovieFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("영화 객체의 단위 테스트")
class MovieTest {

    @ParameterizedTest(name = "순번 할인 결과 - 영화 할인 순번 {0} 상영 순번 {1} 요금 {2}")
    @CsvSource(value = {"1, 1, 9200", "1, 2, 10000", "0, 1, 10000"})
    void 순번에_해당되면_요금할인을_받는다(final int sequenceOfMovie, final int sequenceOfScreening, final long amount) {

        final Movie 아바타 = MovieFixture.createMovie("아바타", 120, 10_000, 800, sequenceOfMovie);
        final Screening screening = new Screening(아바타, sequenceOfScreening, null);

        final Money 영화금액 = 아바타.calculateMovieFee(screening);

        assertThat(영화금액).isEqualTo(Money.wons(amount));
    }

    @ParameterizedTest(name = "영화 아바타가 {0}:{1} 에 상영한 영화의 금액은 {2}이다.")
    @CsvSource(value = {"9, 59, 10000", "10, 00, 9200", "11, 59, 9200", "12, 00, 10000"})
    void 기간에_해당되면_요금할인을_받는다(final int hour, final int minute, final long money) {

        final Movie 아바타 = MovieFixture.createMovie("아바타", 120, 10_000, 800
                , DayOfWeek.MONDAY, 10, 0, 11, 59);
        final Screening screening = new Screening(아바타, 0, LocalDateTime.of(2023, Month.AUGUST, 28, hour, minute));

        final Money 영화금액 = 아바타.calculateMovieFee(screening);

        assertThat(영화금액).isEqualTo(Money.wons(money));
    }
}