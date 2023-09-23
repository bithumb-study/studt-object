package com.object.ch04.movie.after;

import com.object.ch04.domain.Money;
import com.object.ch04.movie.after.condition.DiscountCondition;
import com.object.ch04.movie.after.policy.DiscountPolicyFactory;
import com.object.ch04.movie.after.policy.DiscountPolicy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.object.ch04.domain.Money.*;
import static com.object.ch04.movie.DiscountConditionType.*;
import static com.object.ch04.movie.after.MovieType.*;

public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition> discountConditions;

    private MovieType movieType;
    private Money discountAmount;
    private double discountPercent;
    private DiscountPolicyFactory discountPolicyFactory;


    public Movie(final String title, final Duration runningTime, final Money fee, final double discountPercent
            , final DiscountCondition... discountConditions) {
        this(PERCENT_DISCOUNT, title, runningTime, fee, ZERO, discountPercent, discountConditions);
    }

    public Movie(final String title, final Duration runningTime, final Money fee, final Money discountAmount
            , final DiscountCondition... discountConditions) {
        this(AMOUNT_DISCOUNT, title, runningTime, fee, discountAmount, 0, discountConditions);
    }

    public Movie(final String title, final Duration runningTime, final Money fee) {
        this(NONE_DISCOUNT, title, runningTime, fee, ZERO, 0);
    }

    private Movie(final MovieType movieType, final String title, final Duration runningTime, final Money fee
            , final Money discountAmount, final double discountPercent, final DiscountCondition... discountConditions) {
        this.movieType = movieType;
        this.title = title;
        this.runningTime = runningTime;
        this.fee = fee;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.discountConditions = Arrays.asList(discountConditions);
    }

    public Money calculateDiscountedFee(final MovieType movieType, final LocalDateTime whenScreened, final int sequence) {
        final DiscountPolicy discountPolicy = discountPolicyFactory.getPolicy(movieType);
        return discountPolicy.discount(this, whenScreened, sequence);
    }

    public boolean isDiscountable(final LocalDateTime whenScreened, final int sequence) {

        for (DiscountCondition condition : discountConditions) {
            if (condition.canMatch(PERIOD)) {
                if (condition.isDiscountable(whenScreened.getDayOfWeek(), whenScreened.toLocalTime())) {
                    return true;
                }
            } else {
                if (condition.isDiscountable(sequence)) {
                    return true;
                }
            }
        }

        return false;
    }

    public MovieType getMovieType() {
        return movieType;
    }

    public Money getFee() {
        return fee;
    }

    public Money getDiscountAmount() {
        return discountAmount;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }
}
