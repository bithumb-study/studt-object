package com.object.ch04.movie.after.policy;

import com.object.ch04.domain.Money;
import com.object.ch04.movie.after.Movie;
import com.object.ch04.movie.after.MovieType;

import java.time.LocalDateTime;

public class NoneDiscountPolicy extends DiscountPolicy {

    @Override
    protected MovieType getMovieType() {
        return MovieType.NONE_DISCOUNT;
    }

    @Override
    protected Money calculate(final Movie movie, final LocalDateTime whenScreened, final int sequence) {
        return movie.getFee();
    }
}
