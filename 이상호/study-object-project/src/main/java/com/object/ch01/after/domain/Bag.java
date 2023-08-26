package com.object.ch01.after.domain;

public class Bag {

    private Long amount;
    private Ticket ticket;
    private Invitation invitation;

    public Bag(final Long amount, final Ticket ticket, final Invitation invitation) {
        this.amount = amount;
        this.ticket = ticket;
        this.invitation = invitation;
    }

    public Long hold(final Ticket ticket) {
        if (hasInvitation()) {
            setTicket(ticket);
            return 0L;
        } else {
            setTicket(ticket);
            minusAmount(ticket.getFee());
            return ticket.getFee();
        }
    }

    private boolean hasInvitation() {
        return invitation != null;
    }

    private void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    private void minusAmount(Long amount) {
        this.amount -= amount;
    }
}
