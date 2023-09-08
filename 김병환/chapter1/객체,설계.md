# 📚 1장 객체, 설계

로버트 L. 글래스는 이론보다 실무가 먼저라고 주장한다. 어떤 분야든 초기 단계에서는 아무것도 없는 상태에서 이론을 정립하기보다는 실무를 관찰한 결과를 바탕으로 이론을 정립한다.

- 소프트웨어 개발에서 실무가 이론보다 앞서 있는 대표적인 분야는 소프트웨어 설계/유지보수
- 이 책은 추상적인 개념과 이론보다는 코드를 통해서 객체지향의 다양한 측면을 설명할 것

## 📖 1.1 티켓 판매 애플리케이션 구현하기

```java
public class Invitation {

    private LocalDateTime when; // 초대일자
}
```

- 이벤트 당첨자에게 발송되는 초대장 Class

```java
@Getter
public class Ticket {

    private Long fee;
}
```

- 공연을 관람하기 원하는 모든 사람들은 티켓을 소지해야만 한다.

```java
public class Bag {

    private Long amount; // 현금

    private Invitation invitation;

    @Setter // 초대장을 Ticket으로 교환
    private Ticket ticket;

    // 초대장 없이 현금만 보관
    public Bag(long amount) {
        this(null, amount);
    }

    // 현금과 초대장을 함께 보관
    public Bag(Invitation invitation, Long amount) {
        this.invitation = invitation;
        this.amount = amount;
    }

    public boolean hasInvitation() {
        return invitation != null;
    }

    public boolean hasTicket() {
        return ticket != null;
    }

    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

    public void plusAmount(Long amount) {
        this.amount += amount;
    }
}
```

- 관람객이 소지품을 보관할 가방 Class

```java
@Getter
@AllArgsConstructor
public class Audience {
    
    private Bag bag;
}
```

- 관람객 Class

```java
public class TicketOffice {
    
    private Long amount;
    private List<Ticket> tickets = new ArrayList<>();
    
    public TicketOffice(Long amount, Ticket... tickets) {
        this.amount = amount;
        this.tickets.addAll(Arrays.asList(tickets));
    }
    
    // 첫 번째 위치의 티켓 반환
    public Ticket getTicket() {
        return tickets.remove(0);
    }
    
    public void minusAmount(Long amount) {
        this.amount -= amount;
    }

    public void plusAmount(Long amount) {
        this.amount += amount;
    }
}
```

- 매표소 Class

```java
@Getter
@AllArgsConstructor
public class TicketSeller {

    private TicketOffice ticketOffice;
}
```

- 판매원 Class

```java
@AllArgsConstructor
public class Theater {

    private TicketSeller ticketSeller;

    // 관람객 입장
    public void enter(Audience audience) {
        if (audience.getBag().hasInvitation()) {
            Ticket ticket = ticketSeller.getTicketOffice().getTicket();
            audience.getBag().setTicket(ticket);
            return;
        }
        Ticket ticket = ticketSeller.getTicketOffice().getTicket();
        audience.getBag().minusAmount(ticket.getFee());
        ticketSeller.getTicketOffice().plusAmount(ticket.getFee());
        audience.getBag().setTicket(ticket);
        
    }
}
```

- 소극장 Class

위의 프로그램은 몇 가지 문제가 있다.

## 📖 1.2 무엇이 문제인가

소프트웨어 모듈에는 세 가지 목적이 있다.

1. 실행 중에 제대로 동작
2. 변경을 위해 존재
3. 코드를 읽는 사람과 의사소통

### 🔖 1.2.1 예상을 빗나가는 코드

- Theater 클래스의 enter method
  - 관람객과 판매원이 소극장의 통제를 받는 수동적인 존재
    - 소극장이 가방을 마음대로 여는 행위
  - 여러가지 세부적인 내용들을 한꺼번에 기억하고 있어야함.
    - 각 Class의 행위
    - 하나의 클래스나 메서드에서 너무 많은 세부사항을 다루기 때문
- 이해 가능한 코드란 **그 동작이 우리의 예상에서 크게 벗어나지 않는 코드**

### 🔖 1.2.2 변경에 취약한 코드

- Audience와 TicketSeller를 변경할 경우 Theater도 함께 변경해야 한다
  - 즉, 변경에 취약하다.
- 객체 사이의 **의존성(dependency)** 과 관련된 문제
  - 객체 사이의 의존성을 완전히 없애는 것이 정답은 아니다.
  - 객체지향 설계는 서로 의존하면서 협력하는 객체들의 공동체를 구축하는 것
  - 애플리케이션의 기능을 구현하는 데 필요한 최소한의 의존성만 유지하고 불필요한 의존성을 제거
- 객체 사이의 의존성이 과한 경우를 가리켜 **결합도(coupling)** 가 높다고 한다.

## 📖 1.3 설계 개선하기

위 코드들은 소프트웨어 모듈의 목적 중 2,3번을 만족시키지 못한다.

❗️ 관람객과 판매원을 자율적인 존재로 만들자

### 🔖 1.3.1 자율성을 높이자

```java
@AllArgsConstructor
public class TicketSeller {

    private TicketOffice ticketOffice;
    
    public void sellTo(Audience audience) {
        if (audience.getBag().hasInvitation()) {
            Ticket ticket = ticketOffice.getTicket();
            audience.getBag().setTicket(ticket);
            return;
        }
        Ticket ticket = ticketOffice.getTicket();
        audience.getBag().minusAmount(ticket.getFee());
        ticketOffice.plusAmount(ticket.getFee());
        audience.getBag().setTicket(ticket);
    }
}
```

- ticketOffice에 대한 접근은 오직 TicketSeller 안에만 존재하게 된다.
- 개념적이나 물리적으로 객체 내부의 세부적인 사항을 감추는 것을 캡슐화(encapsulation) 라고 한다.

```java
@AllArgsConstructor
public class Theater {

    private TicketSeller ticketSeller;

    public void enter(Audience audience) {
        ticketSeller.sellTo(audience);
    }
}
```

- Theater는 오직 TicketSeller의 interface에만 의존
- TicketSeller가 내부에 TicketOffice 인스턴스를 포함하는 것은 implementation에 속한다.
- 하지만 Audience는 여전히 자율적인 존재가 아니다.

```java
@AllArgsConstructor
public class Audience {

    private Bag bag;

    public Long buy(Ticket ticket) {
        if (bag.hasInvitation()) {
            bag.setTicket(ticket);
            return 0L;
        }
        bag.setTicket(ticket);
        bag.minusAmount(ticket.getFee());
        return ticket.getFee();
    }
}
```

- Bag을 내부로 캡슐화

```java
@AllArgsConstructor
public class TicketSeller {

    private TicketOffice ticketOffice;

    public void sellTo(Audience audience) {
        ticketOffice.plusAmount(audience.buy(ticketOffice.getTicket()));
    }
}
```

- TicketSeller와 Audience 사이의 결합도 ⬇️
- TicketSeller와 Audience가 자율적인 존재가 됨.
  - 자신의 문제를 스스로 책임지고 해결

### 🔖 1.3.2 무엇이 개선됐는가

- 코드를 읽는 사람과의 의사소통 ⬆️
  - Audience와 TicketSeller는 자신이 가지고 있는 소지품을 스스로 관리
  - 이는, 우리의 예상과 일치함(현실세계에 대응)
- 변경용이성 개선 ⬆️
  - Audience와 TicketSeller의 내부 구현을 변경하더라도 Theater를 함께 변경할 필요가 없음

### 🔖 1.3.3 어떻게 한 것인가

- 자기 자신의 문제를 스스로 해결하도록 코드를 변경
- 객체의 자율성을 높이는 방향으로 개선

### 🔖 1.3.4 캡슐화와 응집도

- 객체 내부의 상태를 캡슐화하고 객체 간에 오직 메시지를 통해서만 상호작용하도록 하자.
- 밀접하게 연관된 작업만을 수행하고 연관성 없는 작업은 다른 객체에게 위임하는 객체를 가리켜 응집도(cohesion)**가 높다고 한다.
  - 객체 스스로 자신의 데이터를 책임져야 한다.

### 🔖 1.3.5 절차지향과 객체지향

최초의 코드는 절차적 프로그래밍 방식으로 작성된 전형적인 구조

- Theater의 enter method는 프로세스(Process)
- Audience, TicketSeller, Bag, TicketOffice는 데이터(Data)
- 프로세스와 데이터를 별도의 모듈에 위치시키는 방식이 **절차적 프로그래밍(Procedural Programming)

수정된 코드는 객체지향 프로그래밍 방식으로 작성

- 데이터를 사용하는 프로세스가 데이터를 소유하고 있는 Audience와 TicketSeller 내부로 이동
- 데이터와 프로세스가 동일한 모듈 내부에 위치하도록 프로그래밍하는 방식이 객체지향 프로그래밍(Object-Oriented Programming)
- 자신의 문제를 스스로 처리해야 한다는 우리의 예상을 만족시켜줌 ▶️ 이해하기 쉬움
- 객체 내부의 변경이 객체 외부에 파급되지 않도록 제어할 수 있음 ▶️ 변경용이성 높음

### 🔖 1.3.6 책임의 이동

- 절차지향과 객체지향의 근본적인 차이는 **책임의 이동**이다.
  - 객체지향 설계에서는 독재자가 존재하지 않고 각 객체에 책임이 적절하게 분배되어 **자신을 스스로 책임**진다.
- 불필요한 세부사항을 캡슐화하는 자율적인 객체들이 **낮은 결합도와 높은 응집도**를 가지고 협력하도록 최소한의 의존성만을 남기는 것이 훌륭한 객체지향 설계다.

### 🔖 1.3.7 더 개선할 수 있다

```java
public class Bag {

    private Long amount;

    private Invitation invitation;

    @Setter
    private Ticket ticket;

    public Long hold(Ticket ticket) {
        if (hasInvitation()) {
            setTicket(ticket);
            return 0L;
        }
        setTicket(ticket);
        minusAmount(ticket.getFee());
        return ticket.getFee();
    }

    public boolean hasInvitation() {
        return invitation != null;
    }

    public void minusAmount(Long amount) {
        this.amount -= amount;
    }
}
```

- Bag을 자율적인 존재로 변경
- 접근제어자 public -> private 변경

```java
@AllArgsConstructor
public class Audience {

    private Bag bag;

    public Long buy(Ticket ticket) {
        return bag.hold(ticket);
    }
}
```

- Audience를 Bag의 interface에만 의존하도록 수정

```java
public class TicketOffice {

    private Long amount;
    private List<Ticket> tickets = new ArrayList<>();

    public TicketOffice(Long amount, Ticket... tickets) {
        this.amount = amount;
        this.tickets.addAll(Arrays.asList(tickets));
    }
    
    public void sellTicketTo(Audience audience) {
        plusAmount(audience.buy(getTicket()));
    }

    private Ticket getTicket() {
        return tickets.remove(0);
    }

    private void plusAmount(Long amount) {
        this.amount += amount;
    }
}
```

- TicketOffice 자율적인 존재로 변경
- 접근제어자 public -> private 변경

```java
@AllArgsConstructor
public class TicketSeller {

    private TicketOffice ticketOffice;

    public void sellTo(Audience audience) {
        ticketOffice.sellTicketTo(audience);
    }
}
```

- TicketSeller가 TicketOffice의 구현이 아닌 interface에 의존

위 코드는 TicketOffice와 Audience 사이에 의존성이 추가되어 결합도가 상승했다. 하지만, TicketOffice의 자율성은 높였다.

1. 어떤 기능을 설계하는 방법은 한 가지 이상일 수 있다.
2. 동일한 기능을 한 가지 이상의 방법으로 설계할 수 있기 때문에 설계는 trade-off의 산물이다.
3. 어떠한 경우에도 모든 사람들을 만족시킬 수 있는 설계를 만들 수는 없다.
4. **설계는 균형의 예술**이다.

### 🔖 1.3.7 그래, 거짓말이다❗️

현실에서는 수동적인 존재라고 하더라도 일단 객체지향의 세계에 들어오면 모든 것이 능동적이고 자율적인 존재로 바뀐다.

- 의인화(anthropomorphism): 능동적이고 자율적인 존재로 소프트웨어 객체를 설계하는 원칙

## 📖 1.4 객체지향 설계

### 🔖 1.4.1 설계가 왜 필요한가

> 설계란 코드를 배치하는 것이다

- 설계는 코드를 작성하는 매 순간 코드를 어떻게 배치할 것인지를 결정하는 과정에서 나온다.
- 설계는 코드 작성의 일부

좋은 설계란 무엇인가?

- 오늘 요구하는 기능을 온전히 수행하면서 내일의 변경을 매끄럽게 수용할 수 있는 설계
  - 변경을 수용할 수 있는 설계
  - 요구사항이 항상 변경되기 때문
  - 코드를 변경할 때 버그가 추가될 가능성이 높기 때문

### 🔖 1.4.2 객체지향 설계

- 객체지향 프로그래밍은 의존성을 효율적으로 통제할 수 있는 다양한 방법을 제공함으로써 요구사항 변경에 좀 더 수월하게 대응할 수 있는 가능성을 높여준다.
- 객체지향 패러다임은 여러분이 세상을 바라보는 방식대로 코드를 작성할 수 있게 돕는다.
- 훌륭한 객체지향 설계란 협력하는 객체 사이의 의존성을 적절하게 관리하는 설계
  - 객체 간의 의존성은 애플리케이션을 수정하기 어렵게 만드는 주범
