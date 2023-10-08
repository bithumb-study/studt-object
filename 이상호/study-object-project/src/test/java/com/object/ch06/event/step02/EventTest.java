package com.object.ch06.event.step02;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.DayOfWeek.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("명령-쿼리 분리 후 반복 회의 테스트")
class EventTest {

    @Test
    void 반복_회의_테스트() {

        final LocalTime localTime = LocalTime.of(10, 30);
        final LocalDateTime localDateTime = LocalDateTime.of(2019, 5, 9, 10, 30);
        final Duration duration = Duration.ofMinutes(30);
        final RecurringSchedule 매주_수요일_10시_30분_회의_30분 = new RecurringSchedule("회의", WEDNESDAY, localTime, duration);
        final Event meeting = new Event("회의", localDateTime, duration);

        assertThat(meeting.isSatisfied(매주_수요일_10시_30분_회의_30분)).isFalse();
        assertThat(meeting.isSatisfied(매주_수요일_10시_30분_회의_30분)).isFalse();
    }
}
