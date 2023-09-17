package com.object.ch04.movie.after;

import com.object.ch04.domain.Money;

import java.time.LocalDateTime;

public class Screening {
    private Movie movie;
    private int sequence;
    private LocalDateTime whenScreened;

    public Screening(final Movie movie, final int sequence, final LocalDateTime whenScreened) {
        this.movie = movie;
        this.sequence = sequence;
        this.whenScreened = whenScreened;
    }

    public Money calculateFee(final int audienceCount) {

        return movie.calculateDiscountedFee(movie.getMovieType(), whenScreened, sequence).times(audienceCount);
    }
}
