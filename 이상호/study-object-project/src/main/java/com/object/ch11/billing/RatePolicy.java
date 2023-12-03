package com.object.ch11.billing;

import com.object.ch11.money.Money;

public interface RatePolicy {
    Money calculateFee(Phone phone);
}
