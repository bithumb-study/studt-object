package com.object.ch02.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Money {

    public static final Money ZERO = Money.wons(0);

    private final BigDecimal amount;

    Money(final BigDecimal amount) {
        this.amount = amount;
    }

    public static Money wons(final long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money wons(final double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public Money plus(final Money amount) {
        return new Money(this.amount.add(amount.amount));
    }

    public Money minus(final Money amount) {
        return new Money(this.amount.subtract(amount.amount));
    }

    public Money times(final double percent) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(percent)));
    }

    public boolean isLessThan(final Money other) {
        return amount.compareTo(other.amount) < 0;
    }

    public boolean isGreaterThanOrEqual(final Money other) {
        return amount.compareTo(other.amount) >= 0;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Money)) {
            return false;
        }

        Money other = (Money)object;
        return Objects.equals(amount.doubleValue(), other.amount.doubleValue());
    }

    public int hashCode() {
        return Objects.hashCode(amount);
    }

    public String toString() {
        return amount.toString() + "원";
    }
}