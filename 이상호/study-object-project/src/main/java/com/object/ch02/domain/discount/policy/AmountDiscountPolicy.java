package com.object.ch02.domain.discount.policy;

import com.object.ch02.domain.Money;
import com.object.ch02.domain.Screening;
import com.object.ch02.domain.discount.DiscountCondition;
import com.object.ch02.domain.discount.DiscountPolicy;

public class AmountDiscountPolicy extends DiscountPolicy {
    private Money discountAmount;

    public AmountDiscountPolicy(final Money discountAmount, final DiscountCondition... conditions) {
        super(conditions);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money getDiscountAmount(final Screening screening) {
        return discountAmount;
    }
}
