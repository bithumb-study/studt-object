package com.object.ch02.domain;

import com.object.ch02.fixture.MovieFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("상영 객체의 단위 테스트")
class ScreeningTest {

    @Test
    void 영화_상영_정보를_확인한다() {

        final Movie 아바타 = MovieFixture.createMovie("아바타", 120, 10_000, 800, 1);
        final Screening screening = new Screening(아바타, 1, null);

        assertAll(
                () -> assertThat(screening.getMovieFee()).isEqualTo(Money.wons(10_000)),
                () -> assertThat(screening.getStartTime()).isNull(),
                () -> assertThat(screening.isSequence(1)).isTrue()
        );
    }
}