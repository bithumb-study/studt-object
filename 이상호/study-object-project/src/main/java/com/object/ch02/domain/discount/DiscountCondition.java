package com.object.ch02.domain.discount;

import com.object.ch02.domain.Screening;

public interface DiscountCondition {
    boolean isSatisfiedBy(final Screening screening);
}