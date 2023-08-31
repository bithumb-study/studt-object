# 📚 1장 객체, 설계

소프트웨어 설계와 유지보수는 실무가 이론을 훨씬 앞서 있다.

설계나 유지보수를 이야기 할때는 개념과 용어가 아니라 코드 그 자체다.

## 📖 1. 티켓 판매 어플리케이션 구현하기

___

EX) 이벤트 당첨객 과 아닌 사람 아닌 사람은 티켓을 구매 해야 한다.

- 초대장 Invitation DTO

```java
// 언제 초대 되었는지
public record Invitation(LocalDateTime when) {
}
```

- 티켓 클래스

```java
public record Ticket(Long fee) {
}
```

- 관람객 소지품 보관 BAG 클래스

```java

@AllArgsConstructor
//소지품을 보관할수 있는 클래스
public class Bag {
    private Long amount;
    private Invitation invitation;
    private Ticket ticket;

    //초대 받지 못한사람
    public static Bag uninvitedPerson(long amount) {
        return new Bag(amount, null, null);
    }

    public static Bag invitedPerson(Invitation invitation, long amount) {
        return new Bag(amount, invitation, null);
    }

    //초대장을 가지고있는지 판별
    public boolean hasInvitation() {
        return invitation != null;
    }

    //티켓소유 여부 판별
    public boolean hasTicket() {
        return ticket != null;
    }

    // 티켓 교환
    public void exchangeTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    // 현금 감소
    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

    // 현금 감소
    public void plusAmount(Long amount) {
        this.amount -= amount;
    }
}
```

- 관람객 클래스
    - 가방을 소지 하고 있다.

```java
public record Audience(Bag bag) {
}
```

- 매표소 클래스

```java
//판매소
public class TicketOffice {
    private Long amount;
    private final List<Ticket> tickets = new ArrayList<>();

    public TicketOffice(Long amount, Ticket... tickets) {
        this.amount = amount;
        this.tickets.addAll(Arrays.asList(tickets));
    }

    public Ticket getTicket() {
        return tickets.remove(0);
    }

    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

    public void plusAmount(Long amount) {
        this.amount -= amount;
    }
}

```

- 판매원
    - 자신이 일하는 매표소를 알아야 한다.

```java
public record TicketSeller(TicketOffice ticketOffice) {
}
```

![1장 애플리케이션 핵심 클래스](https://github.com/bithumb-study/study-unit-testing/assets/58027908/c65cc61d-5fb5-4eab-972f-8e64352c9058)
애플리케이션 핵심 클래스

- 소극장

```java
public record Theater(TicketSeller ticketSeller) {

    /**
     * 관람객의 가방에 초대권의 유무에 따라 초대권을 줄지 아니면 판매할지 결정하여 입장 시켜주는 메소드
     *
     */
    public void enter(Audience audience) {
        if (audience.bag().hasInvitation()) {
            Ticket ticket = ticketSeller.ticketOffice().getTicket();
            audience.bag().exchangeTicket(ticket);
        } else {
            Ticket ticket = ticketSeller.ticketOffice().getTicket();
            audience.bag().minusAmount(ticket.fee());
            ticketSeller.ticketOffice().plusAmount(ticket.fee());
            audience.bag().exchangeTicket(ticket);
        }
    }
}
```

## 📖 2. 무엇이 문제인가

소프트웨어 모듈이 가져야 하는 세 가지 기능

- 실행중 제대로 동작하는것
    - 모듈의 존재 이유
- 변경을 위해 존재한다.
    - 생명주기 동안 변경되기 때문에 간단한 작업으로도 변경 가능 아니면 개선 해야한다.
- 코드를 읽는 사람과 의사소통 하는것
    - 특별한 훈련 없이도 개발자가 쉽게 이해할수 있어야 한다.

### 🔖 2.1 예상을 빗나가는 코드

위의 소극장의 enter 메소드는 이해하기 힘든 코드 - 상식에 벗어나기 때문에

관람객과 판매원이 소극장의 통제를 받는 수동적인 존재

돈을 받고 증가,감소 하고 티켓을 주고 받고 를 모두 소극장이 수행을 한다.

이해 가능한 코드는 상식에 크게 벗어나지 않는 코드이다.

또다른 이유는 코드를 이해하기 위해 여러가지 세부적인 내용들을 한꺼번에 기억하고 있어야한다.

심각한 문제는 Audience 와 TicketSeller 를 변경할 경우 Theater 도 함께 변경해야 한다

### 🔖 2.2 변경에 취약한 코드

제일 큰 문제는 변경에 취약한 것이다.

예외 상황이 생기면 변경에 취약 EX) 현금대신 신용카드 , 가방을 들고있지 않는것

관람객이 가방을 들고있다는 가정이 바뀌면 Audience 객체에서 Bag 을 제거해야하고 Theater 의 enter 메서드 역시 수정 (audience 의 bag 을 접근하기 떄문에)

! 객체 사이의 의존성과 관련된 문제이다.

- 우리의 목표는 애플리케이션의 기능을 구현하는 데 필요한 최소한의 의존성만 유지하고 불필요한 의존성을 제거하는것이다.

![1장 결합도 높은 소극장](https://github.com/bithumb-study/study-unit-testing/assets/58027908/d60025a3-97c5-407d-86e6-5d6577d495a3)

- 객체 사이의 의존성이 과한 경우를 가리켜 결합도가 높다고 말한다.

## 📖 3. 설계 개선하기

코드를 이해하기 어려운 이유는 Theater 가 관람객의 가방과 판매원의 매표소에 직접 접근하기 때문이다.

- 자신의 일을 스스로 처리하지 못함

해결방법

- Theater 가 Audience 와 TicketSeller 에 관해 너무 세세한 부분을 알지못하게 차단한다.
- 관람객과 판매원을 자율적인 존재로 만들면 된다.

### 🔖 3.1 자율성을 높이자

설계 변경이 어려운 이유는 Theater 가 Audience 와 TicketSeller 뿐만 아니라 Audience 소유의 Bag 과 TicketSeller 가 근무하는 TicketOffice 까지
마음대로 접근 가능하기 때문에

1. Theater 의 enter 메서드에서 TicketOffice 에 접근 하는 모든 코드를 TicketSeller 내부로 숨기는것이다.

```java

@AllArgsConstructor
public class TicketSellerV2 {
    private TicketOffice ticketOffice;

    public void sellTo(Audience audience) {
        if (audience.bag().hasInvitation()) {
            Ticket ticket = ticketOffice.getTicket();
            audience.bag().exchangeTicket(ticket);
        } else {
            Ticket ticket = ticketOffice.getTicket();
            audience.bag().minusAmount(ticket.fee());
            ticketOffice.plusAmount(ticket.fee());
            audience.bag().exchangeTicket(ticket);
        }
    }
}
```

ticketOffice getter 가 제거 되었고 외부에서 TicketOffice 에 접근할수 없게된다.

- 결과적으로 ticketOffice 접근은 오직 ticketSeller 만 접근 가능하여 티켓을 꺼내거나 요금을 적립하는 일을 스스로 수행한다.
- 캡슐화

Theater enter 메서드

```java
public record Theater(TicketSellerV2 ticketSeller) {

    public void enter(Audience audience) {
        ticketSeller.sellTo(audience);
    }
}
```

Theater 는 단지 ticketSeller 의 SellTo 메세지만 응답 오직 인터페이스에만 의존한다.

ticketSeller 내부에 ticketOffice 를 포함하는것은 구현

- 인터페이스와 구현으로 나누고 인터페이스만 공개하는 것은 객체사이의 결합을 낮추고 변경하기 쉬운 코드를 작성하기 위한 기본 설계 원칙

2. Audience 의 Bag 을 분리

```java

@AllArgsConstructor
public class AudienceV2 {
    private Bag bag;

    //관람객이 직접 살수있도록 수정 
    public Long buy(Ticket ticket) {
        if (bag.hasInvitation()) {
            bag.exchangeTicket(ticket);
            return 0L;
        } else {
            bag.exchangeTicket(ticket);
            bag.minusAmount(ticket.fee());
            return ticket.fee();
        }
    }
}
```

```java

@AllArgsConstructor
public class TicketSellerV3 {
    private TicketOffice ticketOffice;

    public void sellTo(AudienceV2 audience) {
        ticketOffice.plusAmount(audience.buy(ticketOffice.getTicket()));
    }
}
```

### 🔖 3.2 무엇이 개선

관람객과 판매원이 자신이 가지고 있는 소지품을 스스로 관리한다.

관람객 판매원의 내부구현을 변경하더라도 소극장을 변경할 필요가 없다.

### 🔖 3.3 어떻게 한 것인가

audience 와 ticketSeller 는 티켓을 사고 팔고 의 문제를 직접하여 사소한 변경에도 Theater 를 변경하지 않아도 된다.

객체의 자율성을 높이는 방향으로 설계했다.

### 🔖 3.4 캡슐화와 응집도

객체 내부의 상태를 캡슐화 하고 객체 간에 오직 메세지를 통해서만 상호작용하도록 만드는것

응집도

- 연관된 작업만 수행하고 연과성 없는 작업은 다른 객체에게 위임하는객체를 가리켜 응집도가 높은 객체라 할수있다.
- 응집도를 높이기 위해서는 객체 스스로 자신의 데이터를 책임져야 한다.

### 🔖 3.5 절차지향과 객체지향

Theater 의 enter 메서드는 process 이고 Audience, TicketSeller, Bag 은 데이터

이처럼 프로세스와 데이터를 별도의 모듈에 위치하는 것을 절차적 프로그래밍

- 전형적인 의존성 구조
- 마지막 theater 가 모든처리를 담당하고 나머지 클래스는 단지 데이터의 역할만 수행하기 떄문
- 변경에 취약하다.

객체지향 프로그래밍

- 데이터와 프로세스가 동일한 모듈 내부에 위치하도록 프로그래밍하는 방식
- 캡슐화와 의존성을 적절히 관리함으로써 객체 사이의 결합도를 낮추는것

### 🔖 3.6 책임의 이동

![image](https://github.com/bithumb-study/study-unit-testing/assets/58027908/ccb6bcf2-6781-4fa0-bb35-37aa232fa3bb)


절차지향은 theater 에 책임이 집중되어 있다.

객체지향은 theater 에  몰려있던 책임이 다른 객체에게 분산되어 있다.

### 🔖 3.7 더 개선할 수 있다.

Bag 의 내부상태에 접근하는 모든 메서드를 Bag안으로 캡슐화 한다.

```java
@AllArgsConstructor

public class BagV2 {
    private Long amount;
    private Invitation invitation;
    private Ticket ticket;

    public Long hold(Ticket ticket) {
        if (hasInvitation()) {
            exchangeTicket(ticket);
            return 0L;
        } else {
            exchangeTicket(ticket);
            minusAmount(ticket.fee());
            return ticket.fee();
        }
    }
    
    //초대장을 가지고있는지 판별
    public boolean hasInvitation() {
        return invitation != null;
    }
    // 티켓 교환
    public void exchangeTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public void minusAmount(Long amount) {
        this.amount -= amount;
    }
}
```

```java
@AllArgsConstructor
public class AudienceV2 {
    private BagV2 bag;

    public Long buy(Ticket ticket) {
        return bag.hold(ticket);
    }
}

```

ticketOffice 역시 자율권 을 준다.

ticketSeller 가 헀던 표를 사고 팔고를 TicketOffice 가 하게된다.

```java
@AllArgsConstructor
public class TicketSellerV3 {
    private TicketOffice ticketOffice;

    public void sellTo(AudienceV2 audience){
        ticketOffice.sellTicketTo(audience);
    }
}

```

1. 어떤 기능을 설계하는 방법은 한가지 이상일 수 있다.
2. 동일한 기능을 한 가지 이상의 방법으로 설계할 수 있기 때문에 결국 설계는 트레이드 오프의 산물

### 🔖 3.8 그래, 거짓말이다! 

실생활 에서 관람객과 판매원은 티켓을 사고 팔고 할수있는 상식이 있다.

#### 의인화

가방과 판매소는 아니다. 
- 하지만 객체지향 세계에 오면 모든 것이 능동적 자율적 존재로 바뀐다.

## 📖 4. 객체지향 설계

### 🔖 4.1 설계가 왜 필요한가

오늘 요구하는 기능을 온전히 수행하면서 내일 변경을 매끄럽게 수용할 수 있는 설계

### 🔖 4.2 객체 지향 설계

변경가능한 코드란 이해하기 쉬운 코드 

객체 역시 자신의 데이터를 스스로 책임지는 자율적인 존재

훌륭한 객체지향 설계란 협력하는 객체 사이의 의존성을 적절하게 관리하는 설계









