package com.object.ch04.movie.after;

import com.object.ch04.domain.Money;
import com.object.ch04.movie.Customer;

public class ReservationAgency {

    public Reservation reserve(Screening screening, Customer customer, int audienceCount) {
        Money fee = screening.calculateFee(audienceCount);
        return new Reservation(customer, screening, fee, audienceCount);
    }
}