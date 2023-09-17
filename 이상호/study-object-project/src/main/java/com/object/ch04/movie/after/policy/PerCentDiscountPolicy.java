package com.object.ch04.movie.after.policy;

import com.object.ch04.domain.Money;
import com.object.ch04.movie.after.Movie;
import com.object.ch04.movie.after.MovieType;

import java.time.LocalDateTime;

public class PerCentDiscountPolicy extends DiscountPolicy {

    @Override
    protected MovieType getMovieType() {
        return MovieType.PERCENT_DISCOUNT;
    }

    @Override
    protected Money calculate(final Movie movie, final LocalDateTime whenScreened, final int sequence) {
        if (!movie.isDiscountable(whenScreened, sequence)) {
            return movie.getFee();
        }
        return movie.getFee().minus(movie.getFee().times(movie.getDiscountPercent()));
    }
}
