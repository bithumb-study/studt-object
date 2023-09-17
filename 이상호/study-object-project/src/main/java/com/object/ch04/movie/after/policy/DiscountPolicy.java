package com.object.ch04.movie.after.policy;

import com.object.ch04.domain.Money;
import com.object.ch04.movie.after.Movie;
import com.object.ch04.movie.after.MovieType;

import java.time.LocalDateTime;

public abstract class DiscountPolicy {

    protected abstract MovieType getMovieType();
    protected abstract Money calculate(final Movie movie, final LocalDateTime whenScreened, final int sequence);

    public Money discount(final Movie movie, final LocalDateTime whenScreened, final int sequence) {
        return calculate(movie, whenScreened, sequence);
    }
}
