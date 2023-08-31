package com.object.ch01.after.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TicketOffice {

    private Long amount;

    private List<Ticket> tickets = new ArrayList<>();

    public TicketOffice(final Long amount, final int countOfTicket) {
        this.amount = amount;
        this.tickets.addAll(create(countOfTicket));
    }

    public void sellTicketTo(final Audience audience) {
        plusAmount(audience.buy(getTicket()));
    }

    private List<Ticket> create(final int countOfTicket) {

        final List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < countOfTicket; i++) {
            tickets.add(Ticket.createOfDefault());
        }
        return tickets;
    }

    private Ticket getTicket() {
        return tickets.remove(0);
    }

    private void plusAmount(final Long amount) {
        this.amount += amount;
    }

    public Long getAmount() {
        return amount;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public int remainingOfTicket() {
        return tickets.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketOffice that = (TicketOffice) o;
        return Objects.equals(amount, that.amount) && Objects.equals(tickets, that.tickets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, tickets);
    }
}
