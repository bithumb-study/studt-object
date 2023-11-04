package com.object.ch08.movie.pricing;

import com.object.ch08.money.Money;
import com.object.ch08.movie.DiscountPolicy;
import com.object.ch08.movie.Screening;

import java.util.Arrays;
import java.util.List;

public class OverlappedDiscountPolicy extends DiscountPolicy {
    private List<DiscountPolicy> discountPolicies;

    public OverlappedDiscountPolicy(DiscountPolicy ... discountPolicies) {
        this.discountPolicies = Arrays.asList(discountPolicies);
    }

    @Override
    protected Money getDiscountAmount(Screening screening) {
        Money result = Money.ZERO;
        for (DiscountPolicy each : discountPolicies) {
            result = result.plus(each.calculateDiscountAmount(screening));
        }
        return result;
    }
}
