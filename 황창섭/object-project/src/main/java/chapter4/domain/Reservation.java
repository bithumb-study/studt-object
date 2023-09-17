package chapter4.domain;

@AllArgsConstruct
@Getter
@Setter
public class Reservation {
    private Customer customer;
    private Screening screening;
    private Money fee;
    private int audienceCount;
}
