package chapter1.before;

// 관람객의 소지품
public class Bag {
    private Long amount;
    private Invitation invitation;
    private Ticket ticket;

    /* 이벤트에 당첨도니 관람객의 가방 안에는 현금과 초대장이
    * 들어있지만 이벤트에 당첨되지 않은 관람객의 가방 안에는 초대장이
    * 들어있지 않음을 표현하는 생성자
    */
    public Bag(long amount){
        this(null, amount);
    }

    public Bag(Invitation invitation, long amount){
        this.invitation = invitation;
        this.amount = amount;
    }

    public boolean hasInvitation(){
        return invitation != null;
    }

    public boolean hasTicket(){
        return ticket != null;
    }

    public void setTicket(Ticket ticket){
        this.ticket = ticket;
    }

    public void minusAmount(Long amount){
        this.amount -= amount;
    }

    public void plusAmount(Long amount){
        this.amount += amount;
    }
}
