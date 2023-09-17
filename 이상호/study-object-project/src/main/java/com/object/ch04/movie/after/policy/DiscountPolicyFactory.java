package com.object.ch04.movie.after.policy;

import com.object.ch04.movie.after.MovieType;

import java.util.EnumMap;
import java.util.Optional;

public class DiscountPolicyFactory {

    private EnumMap<MovieType, DiscountPolicy> map = null;

    public DiscountPolicy getPolicy(final MovieType movieType) {

        init();
        return Optional.ofNullable(map.get(movieType))
                .orElseThrow(IllegalArgumentException::new);
    }

    private void init() {
        if (map == null) {
            map = new EnumMap(MovieType.class);
            map.put(MovieType.AMOUNT_DISCOUNT, new AmountDiscountPolicy());
            map.put(MovieType.PERCENT_DISCOUNT, new PerCentDiscountPolicy());
            map.put(MovieType.NONE_DISCOUNT, new NoneDiscountPolicy());
        }
    }
}
