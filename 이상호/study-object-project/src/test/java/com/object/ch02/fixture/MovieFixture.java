package com.object.ch02.fixture;

import com.object.ch02.domain.Money;
import com.object.ch02.domain.Movie;
import com.object.ch02.domain.discount.condition.PeriodCondition;
import com.object.ch02.domain.discount.condition.SequenceCondition;
import com.object.ch02.domain.discount.policy.AmountDiscountPolicy;
import com.object.ch02.domain.discount.policy.PercentDiscountPolicy;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;

public class MovieFixture {

    public static Movie createMovie(final String title, final long duration, final long amount) {

        return new Movie(title, Duration.ofMinutes(duration), Money.wons(amount)
                , null);
    }

    public static Movie createMovie(final String title, final long duration, final long amount, final long discount, final int sequence) {

        return new Movie(title, Duration.ofMinutes(duration), Money.wons(amount)
                , new AmountDiscountPolicy(Money.wons(discount)
                , new SequenceCondition(sequence)));
    }

    public static Movie createMovie(final String title, final long duration, final long amount, final double percent, final int sequence) {

        return new Movie(title, Duration.ofMinutes(duration), Money.wons(amount)
                , new PercentDiscountPolicy(percent
                , new SequenceCondition(sequence)));
    }

    public static Movie createMovie(final String title, final long duration, final long amount, final long discount
            , final DayOfWeek dayOfWeek, final int hourOfStart, final int minuteOfStart, final int hourOfEnd, final int minuteOfEnd) {

        return new Movie(title, Duration.ofMinutes(duration), Money.wons(amount)
                , new AmountDiscountPolicy(Money.wons(discount)
                , new PeriodCondition(dayOfWeek, LocalTime.of(hourOfStart, minuteOfStart), LocalTime.of(hourOfEnd, minuteOfEnd))));
    }

    public static Movie createMovie(final String title, final long duration, final long amount, final long discount
            , final PeriodCondition periodCondition) {

        return new Movie(title, Duration.ofMinutes(duration), Money.wons(amount)
                , new AmountDiscountPolicy(Money.wons(discount)
                , periodCondition));
    }
}
