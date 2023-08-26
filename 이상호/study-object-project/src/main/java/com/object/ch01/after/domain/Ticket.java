package com.object.ch01.after.domain;

public class Ticket {

    private static final Long DEFAULT_TICKET_PRICE = 10000L;

    private Long fee;

    private Ticket(final Long balance) {
        this.fee = balance;
    }

    public static Ticket createOfDefault() {
        return new Ticket(DEFAULT_TICKET_PRICE);
    }

    public static Ticket createOfInvitation() {
        return new Ticket(0L);
    }

    public static Ticket create(final Long balance) {
        validate(balance);
        return new Ticket(balance - DEFAULT_TICKET_PRICE);
    }

    private static void validate(final Long balance) {
        if (DEFAULT_TICKET_PRICE > balance) {
            throw new IllegalArgumentException("티켓 구매 금액이 부족합니다.");
        }
    }

    public static Ticket empty() {
        return null;
    }

    public Long getFee() {
        return fee;
    }
}
