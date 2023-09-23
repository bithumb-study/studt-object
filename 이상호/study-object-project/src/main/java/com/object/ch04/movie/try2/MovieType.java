package com.object.ch04.movie.try2;

import com.object.ch04.domain.Money;

public enum MovieType {

    AMOUNT_DISCOUNT {
        @Override
        Money apply(final Money fee, final Money discountAmount, final double discountPercent) {
            return fee.minus(discountAmount);
        }
    },

    PERCENT_DISCOUNT {
        @Override
        Money apply(final Money fee, final Money discountAmount, final double discountPercent) {
            return fee.minus(fee.times(discountPercent));
        }
    },

    NONE_DISCOUNT {
        @Override
        Money apply(final Money fee, final Money discountAmount, final double discountPercent) {
            return fee;
        }
    };

    abstract Money apply(final Money fee, final Money discountAmount, final double discountPercent);
}
