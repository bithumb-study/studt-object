package com.object.ch02.domain.discount.policy;

import com.object.ch02.domain.Money;
import com.object.ch02.domain.Screening;
import com.object.ch02.domain.discount.DiscountCondition;
import com.object.ch02.domain.discount.DiscountPolicy;

public class PercentDiscountPolicy extends DiscountPolicy {
    private double percent;

    public PercentDiscountPolicy(final double percent, final DiscountCondition... conditions) {
        super(conditions);
        this.percent = percent;
    }

    @Override
    protected Money getDiscountAmount(final Screening screening) {
        return screening.getMovieFee().times(percent);
    }
}
