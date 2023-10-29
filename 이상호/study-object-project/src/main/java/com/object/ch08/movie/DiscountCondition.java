package com.object.ch08.movie;

public interface DiscountCondition {
    boolean isSatisfiedBy(Screening screening);
}