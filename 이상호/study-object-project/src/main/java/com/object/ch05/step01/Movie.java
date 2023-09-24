package com.object.ch05.step01;

import com.object.ch05.Money;

import java.time.Duration;
import java.util.List;

public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition> discountConditions;

    private MovieType movieType;
    private Money discountAmount;
    private double discountPercent;

    private Movie(final String title, final Duration runningTime, final Money fee, final List<DiscountCondition> discountConditions
            , final MovieType movieType, final Money discountAmount, final double discountPercent) {
        this.title = title;
        this.runningTime = runningTime;
        this.fee = fee;
        this.discountConditions = discountConditions;
        this.movieType = movieType;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
    }

    private Movie(final String title, final Duration runningTime, final Money fee, final List<DiscountCondition> discountConditions
            , final MovieType movieType, final Money discountAmount) {
        this(title, runningTime, fee, discountConditions, movieType, discountAmount, 0);
    }

    private Movie(final String title, final Duration runningTime, final Money fee, final List<DiscountCondition> discountConditions
            , final MovieType movieType, final double discountPercent) {
        this(title, runningTime, fee, discountConditions, movieType, null, discountPercent);
    }

    public static Movie ofDiscountAmount(final String title, final Duration runningTime, final Money fee, final List<DiscountCondition> discountConditions
            , final MovieType movieType, final Money discountAmount) {

        return new Movie(title, runningTime, fee, discountConditions, movieType, discountAmount, 0);
    }

    public static Movie ofPercentAmount(final String title, final Duration runningTime, final Money fee, final List<DiscountCondition> discountConditions
            , final MovieType movieType, final double discountPercent) {

        return new Movie(title, runningTime, fee, discountConditions, movieType, null, discountPercent);
    }

    public Money calculateMovieFee(Screening screening) {
        if (isDiscountable(screening)) {
            return fee.minus(calculateDiscountAmount());
        }

        return fee;
    }

    private boolean isDiscountable(Screening screening) {
        return discountConditions.stream()
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
