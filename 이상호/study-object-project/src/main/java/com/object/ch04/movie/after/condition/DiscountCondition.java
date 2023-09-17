package com.object.ch04.movie.after.condition;

import com.object.ch04.movie.DiscountConditionType;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class DiscountCondition {
    private DiscountConditionType type;
    private int sequence;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public DiscountCondition(final int sequence){
        this.type = DiscountConditionType.SEQUENCE;
        this.sequence = sequence;
    }

    public DiscountCondition(final DayOfWeek dayOfWeek, final LocalTime startTime, final LocalTime endTime){
        this.type = DiscountConditionType.PERIOD;
        this.dayOfWeek= dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isDiscountable(final DayOfWeek dayOfWeek, final LocalTime time) {
        if (type != DiscountConditionType.PERIOD) {
            throw new IllegalArgumentException();
        }

        return this.dayOfWeek.equals(dayOfWeek) &&
                this.startTime.compareTo(time) <= 0 &&
                this.endTime.compareTo(time) >= 0;
    }

    public boolean isDiscountable(final int sequence) {
        if (type != DiscountConditionType.SEQUENCE) {
            throw new IllegalArgumentException();
        }

        return this.sequence == sequence;
    }

    public boolean canMatch(final DiscountConditionType discountConditionType) {
        return this.type == discountConditionType;
    }
}
