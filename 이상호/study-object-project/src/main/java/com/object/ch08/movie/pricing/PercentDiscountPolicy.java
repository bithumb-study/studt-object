package com.object.ch08.movie.pricing;

import com.object.ch08.money.Money;
import com.object.ch08.movie.DiscountCondition;
import com.object.ch08.movie.DiscountPolicy;
import com.object.ch08.movie.Screening;

public class PercentDiscountPolicy extends DiscountPolicy {
    private double percent;

    public PercentDiscountPolicy(double percent, DiscountCondition... conditions) {
        super(conditions);
        this.percent = percent;
    }

    @Override
    protected Money getDiscountAmount(Screening screening) {
        return screening.getMovieFee().times(percent);
    }
}
