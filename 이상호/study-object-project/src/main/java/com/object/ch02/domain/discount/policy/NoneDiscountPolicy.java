package com.object.ch02.domain.discount.policy;

import com.object.ch02.domain.Money;
import com.object.ch02.domain.Screening;
import com.object.ch02.domain.discount.DiscountPolicy;

public class NoneDiscountPolicy extends DiscountPolicy {
    @Override
    protected Money getDiscountAmount(final Screening screening) {
        return Money.ZERO;
    }
}
