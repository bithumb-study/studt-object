package chapter06;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class RecurringSchedule {
    private String subject;
    private DayOfWeek dayOfWeek;
    private LocalDateTime from;
    private Duration duration;
}
