package com.object.ch02.domain.discount.condition;

import com.object.ch02.domain.Movie;
import com.object.ch02.domain.Screening;
import com.object.ch02.fixture.MovieFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("순번 조건 객체의 단위 테스트")
class SequenceConditionTest {

    @ParameterizedTest(name = "영화 할인 조건 순번({0})과 상영 조건 순번({1}) 일치 결과가 {2}이다.")
    @CsvSource(value = {"1, 1, true", "1, 2, false"})
    void 할인_조건이_순번_조건에_해당하는지_확인한다(final int sequenceOfMovie, final int sequenceOfScreening, final boolean matched) {

        final SequenceCondition 순번조건 = new SequenceCondition(sequenceOfMovie);
        final Movie 아바타 = MovieFixture.createMovie("아바타", 120, 10_000, 800, sequenceOfMovie);
        final Screening 상영 = new Screening(아바타, sequenceOfScreening, null);

        assertThat(순번조건.isSatisfiedBy(상영)).isEqualTo(matched);
    }
}