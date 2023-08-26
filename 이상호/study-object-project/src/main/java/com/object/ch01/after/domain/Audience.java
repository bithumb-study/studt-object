package com.object.ch01.after.domain;

public class Audience {

    private Bag bag;

    public Audience(final Bag bag) {
        this.bag = bag;
    }

    public Long buy(final Ticket ticket) {
        return bag.hold(ticket);
    }
}
