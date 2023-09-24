package com.object.ch05.step02;

import com.object.ch05.Money;

import java.time.Duration;
import java.util.List;

public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;

    private MovieType movieType;
    private Money discountAmount;
    private double discountPercent;

    private List<PeriodCondition> periodConditions;
    private List<SequenceCondition> sequenceConditions;

    public Movie(final String title, final Duration runningTime, Money fee, final MovieType movieType, final Money discountAmount,
                 final double discountPercent, final List<PeriodCondition> periodConditions, final List<SequenceCondition> sequenceConditions) {
        this.title = title;
        this.runningTime = runningTime;
        this.fee = fee;
        this.movieType = movieType;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.periodConditions = periodConditions;
        this.sequenceConditions = sequenceConditions;
    }

    public static Movie ofAmount(final String title, final Duration runningTime, final Money fee, final MovieType movieType
            , final Money discountAmount, final List<PeriodCondition> periodConditions, final List<SequenceCondition> sequenceConditions) {

        return new Movie(title, runningTime, fee, movieType, discountAmount, 0, periodConditions, sequenceConditions);
    }

    public static Movie ofPercent(final String title, final Duration runningTime, final Money fee, final MovieType movieType
            , final double discountPercent, final List<PeriodCondition> periodConditions, final List<SequenceCondition> sequenceConditions) {

        return new Movie(title, runningTime, fee, movieType, null, discountPercent, periodConditions, sequenceConditions);
    }

    public Money calculateMovieFee(Screening screening) {
        if (isDiscountable(screening)) {
            return fee.minus(calculateDiscountAmount());
        }

        return fee;
    }

    private boolean isDiscountable(Screening screening) {
        return checkPeriodConditions(screening) ||
                checkSequenceConditions(screening);
    }

    private boolean checkPeriodConditions(Screening screening) {
        return periodConditions.stream()
                .anyMatch(condition -> condition.isSatisfiedBy(screening));
    }

    private boolean checkSequenceConditions(Screening screening) {
        return sequenceConditions.stream()
                .anyMatch(condition -> condition.isSatisfiedBy(screening));
    }

    private Money calculateDiscountAmount() {
        switch(movieType) {
            case AMOUNT_DISCOUNT:
                return calculateAmountDiscountAmount();
            case PERCENT_DISCOUNT:
                return calculatePercentDiscountAmount();
            case NONE_DISCOUNT:
                return calculateNoneDiscountAmount();
        }

        throw new IllegalStateException();
    }

    private Money calculateAmountDiscountAmount() {
        return discountAmount;
    }

    private Money calculatePercentDiscountAmount() {
        return fee.times(discountPercent);
    }

    private Money calculateNoneDiscountAmount() {
        return Money.ZERO;
    }
}
