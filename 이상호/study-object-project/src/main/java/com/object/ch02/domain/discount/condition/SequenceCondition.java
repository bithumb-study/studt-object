package com.object.ch02.domain.discount.condition;

import com.object.ch02.domain.Screening;
import com.object.ch02.domain.discount.DiscountCondition;

public class SequenceCondition implements DiscountCondition {
    private int sequence;

    public SequenceCondition(final int sequence) {
        this.sequence = sequence;
    }

    public boolean isSatisfiedBy(final Screening screening) {
        return screening.isSequence(sequence);
    }
}
