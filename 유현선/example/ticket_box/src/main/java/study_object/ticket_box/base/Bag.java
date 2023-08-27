package study_object.ticket_box.base;

public class Bag {
    private Long amount;
    private Invitation invitation;
    private Ticket ticket;

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Long hold(Ticket ticket) {
        if (hasInvitation()) {
            setTicket(ticket);
            return 0L;
        } else {
            setTicket(ticket);
            minusAmount(ticket.getFee());
            return ticket.getFee();
        }
    }

    public boolean hasInvitation() {
        return invitation != null;
    }


    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

}
