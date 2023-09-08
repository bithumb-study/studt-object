package com.object.ch02.domain.discount.condition;

import com.object.ch02.domain.Movie;
import com.object.ch02.domain.Screening;
import com.object.ch02.fixture.MovieFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("기간 조건 객체의 단위 테스트")
class PeriodConditionTest {

    @ParameterizedTest(name = "월요일 상영인 영화 아바타가 {0}:{1} 에 상영한 영화의 기간 조건 할인 여부는 {2}이다.")
    @CsvSource(value = {"9, 59, false", "10, 00, true", "20, 59, true", "21, 00, false"})
    void 할인_조건이_기간_조건에_해당하는지_확인한다(final int hour, final int minute, final boolean matched) {

        final PeriodCondition periodCondition = new PeriodCondition(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(20, 59));
        final Movie 아바타 = MovieFixture.createMovie("아바타", 120, 10_000, 800, periodCondition);
        final Screening 상영 = new Screening(아바타, 0, LocalDateTime.of(2023, Month.AUGUST, 28, hour, minute));

        assertThat(periodCondition.isSatisfiedBy(상영)).isEqualTo(matched);
    }
}