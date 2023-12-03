package com.object.ch11.billing;

import com.object.ch11.money.Money;

public abstract class AdditionalRatePolicy implements RatePolicy {
    private RatePolicy next;

    protected AdditionalRatePolicy(RatePolicy next) {
        this.next = next;
    }

    @Override
    public Money calculateFee(Phone phone) {
        Money fee = next.calculateFee(phone);
        return afterCalculated(fee) ;
    }

    protected abstract Money afterCalculated(Money fee);
}
