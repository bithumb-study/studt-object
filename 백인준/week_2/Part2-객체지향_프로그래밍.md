# 📚 2장 객체지향 프로그래밍

가벼운 마음가짐 ^^

## 📖 2.1 영화 예매 시스템

#### 전제조건 , 용어 정리

영화 : 제목, 상영 시간, 가격정보 와같은 영화가 가지고있는 기본적인 정보

상영 : 실제로 관객들이 영화를 관람하는 시간

- 실제로 관람객은 영화를 예매하는 것이 아니라 상영시간을 예매

할인액 두가지

- 할인 조건
    - 순서 조건(순번이 10인경우 할인) , 기간 조건 (상영 시작 시간)
- 할인 정책
    - 금액 할인 정책 (일정 금액 할인) , 비율 할인 정책(일정 비율 요금 할인)

할인 적용은 할인 조건과 할인 정책을 조합해서 사용한다.

## 📖 2.2 객체지향 프로그래밍을 향해

### 🔖 2.2.1 협력, 객체, 클래스

객체지향은 말 그대로 객체를 지향하는 것

- 클래스가 아닌 객체에 초점을 맞출 때에만 얻을수 있다.

1. 클래스 보다 어떤 객체들이 필요한지 고민하라.
    - 클래스는 공통적인 상태와 행동을 공유하는 객체들을 추상화한것
    - 클래스의 윤곽을 잡기위해 어떤 객체들이; 어떤상태와 행동을 가지는지 먼저 결정
    - 객체를 중심에 두는 접근 방법은 설계를 단순하고 깔끔하게 만든다.
2. 객체를 독립적인 존재가 아니라 기능을 구현하기 위해 협력하는 공동체의 일원으로 봐야 한다.
    - 객체는 다른 객체에게 도움을 주거나 의존하면서 살아가는 협력적인 존재
    - 공동체로 바라보는 것이 설계를 유연하고 확장 가능 하게 만든다.

### 🔖 2.2.2 도메인의 구조를 따르는 프로그램 구조

도메인

- 문제를 해결하기 위해 사용자가 프로그램을 사용하는 분야

일반적으로 클래스의 이름은 대응되는 도메인 개념의 이름과 동일하거나 적어도 유사하게 지어야한다.

클래스 사이의 관계도 최대한 도메인 개념 사이에 맺어진 관계와 유사하게 만들어서 프로그램의 구조를 이해하고 예상하기 쉽게 만들어야한다.

![image](https://github.com/bithumb-study/study-object/assets/58027908/c814e2a8-1c75-40b4-84f3-4ee6e5385eec)

### 🔖 2.2.3 클래스 구현하기

```java

@AllArgsConstructor
public class Screening {
    private Movie movie;
    private int sequence;
    private LocalDateTime whenScreened;

    public LocalDateTime getStartTime() {
        return whenScreened;
    }

    public boolean isSequence(int sequence) {
        return this.sequence == sequence;
    }

    public Money getMovieFee() {
        return movie.getFee();
    }
}
```

여기서 주목할 점은 인스턴스의 변수의 가시성은 private 메서드의 가시성은 public 이다.

클래스를 사용할때 가장 중요한것은 클래스의 경계를 구분 짓는 것이다.

클래스는 내부와 외부로 구분 되고 훌륭한 클래스 설계의 핵심은 어떤 부분을 내부 혹은 외부에 공개할지 결정

- 경계의 명확성이 객체의 자율성을 보장
- 프로그래머에게 구현의 자유를 제공 하기 때문

#### 자율적인 객체

두가지 사실

1. 객체가 상태와 행동을 함께 가지는 복합적인 존재
2. 객체가 스스로 판단하고 행동하는 자율적인 존재

캡슐화

- 데이터와 기능을 객체 내부로 함께 묶는 것

접근 제어

- 외부에서의 접근을 통제
- 접근 수정자를 통해 사용

객체 내부로의 접근을 통제하는 이유는 객체를 자율적인 존재로 만들기 위함

객체지향의 핵심 스스로 상태를 관리하고 판단,행동하는 자율적인 객체들을 구성하기 위해 외부의 간섭을 최소화해야한다.

퍼블릭 인터페이스(public interface)

- 외부에서 접근 가능한 부분

구현 (implementation)

- 외부 에서는 접근 불가능 하고 오직 내부에서만 접근 가능한 부분

인터페이스와 구현의 분리원칙은 객체지향 프로그램의 핵심 원칙

#### 프로그래머의 자유

클래스 작성자

- 새로운 데이터 타입을 프로그램에 추가

클라이언트 프로그래머

- 클래스 작성자가 추가한 데이터 타입을 사용

클래스 작성자는 클라이언트 프로그래머에게 필요한 부분만 공개하고 나머지는 숨긴다.

이를 구현 은닉이라 한다.

### 🔖 2.2.4 협력하는 객체들의 공동체

```java
public class Screening {
    /**
     *
     * @param customer 예매자
     * @param audienceCount  인원수
     * @return 예매정보
     */
    public Reservation reserve(Customer customer, int audienceCount) {
        return new Reservation(customer, this, calculateFee(audienceCount), audienceCount);
    }

    /**
     *
     * @param audienceCount 인원수
     * @return 영화 가격 * 인원수 만큼의 값
     */
    public Money calculateFee(int audienceCount) {
        return movie.calculateMovieFee(this).times(audienceCount);
    }
}
```

```java

@AllArgsConstructor
public class Money {
    public static final Money ZERO = Money.wons(0);

    private final BigDecimal amount;

    public static Money wons(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money wons(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public Money plus(Money money) {
        return new Money(this.amount.add(money.amount));
    }

    public Money minus(Money money) {
        return new Money(this.amount.subtract(money.amount));
    }

    public Money times(double percent) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(percent)));
    }

    public boolean isLessThan(Money money) {
        return amount.compareTo(money.amount) < 0;
    }

    public boolean isGreaterThanOrEqual(Money money) {
        return amount.compareTo(money.amount) >= 0;
    }

}

```

객체를 사용해서 해당 개념을 구현

```java

@AllArgsConstructor
public class Reservation {
    private Customer customer;
    private Screening screening;
    private Money fee;
    private int audienceCount;
}

```

영화를 예매 하기위해 Screening , Reservation , Movie 인스턴트들은 서로의 메서드를 호출 하면서 상호자용

협력

- 어떤 기능을 구현하기 위해 객체들 사이에 이뤄지는 상호작용

### 🔖 2.2.5 협력에 관한 짧은 이야기

객체가 다른 객체와 상호작용할 수 있는 유일한 방법은 메세지를 전송 ,수신 하는것뿐

이처럼 수신된 메세지를 처리하기 위한 자신만의 방법을 메서드라고 한다.

메시지와 메서드의 구분에서부터 다형성의 개념이 출발한다.

## 📖 2.3 할인 요금 구하기

### 🔖 2.3.1 할인 요금 계산을 위한 협력 시작하기

```java

@AllArgsConstructor
public class Movie {
    private String title;
    private Duration runningTime;
    @Getter
    private Money fee;
    private DiscountPolicy discountPolicy;

    public Money calculateMovieFee(Screening screening) {
        return fee.minus(discountPolicy.calculateDiscountAmount(screening));
    }
}
```

할인 정책 판단하는 것은 discountPolicy 에게 메세지를 전달하는 것 뿐

- 상속, 다형성 , 추상화

### 🔖 2.3.1 할인 정책과 할인 조건

AmountDiscountPolicy , PercentDiscountPolicy 클래스로 구현 두 코드가 유사 계산방식 만 다르기 때문에
중복제거 위해 공통코드를 둘 부모 클래스인 DiscountPolicy 를 생성

```java
public abstract class DiscountPolicy {

    private List<DiscountCondition> conditions = new ArrayList<>();

    public DiscountPolicy(DiscountCondition... conditions) {
        this.conditions = Arrays.asList(conditions);
    }

    public Money calculateDiscountAmount(Screening screening) {
        for (DiscountCondition each : conditions) {
            if (each.isSatisfiedBy(screening)) {
                return getDiscountAmount(screening);
            }
        }
        return Money.ZERO;
    }

    abstract protected Money getDiscountAmount(Screening screening);
}
```

실제로 요금을 계산하는 부분은 추상 메서드인 getDiscountAmount 메서드에게 위임

- 자식클래스에서 오버라이딩 한 메서드가 실행한다.

TEMPLATE METHOD 패턴

- 기본적인 알고리즘은 추상클래스에 흐름을 구현하고 필요한 처리를 자식클래스에게 위임

```java
/**
 * 순서 조건 할인
 */
@AllArgsConstructor
public class SequenceCondition implements DiscountCondition {
    private int sequence;

    @Override
    public boolean isSatisfiedBy(Screening screening) {
        return screening.isSequence(sequence);
    }
}
```

```java
/**
 * 기간 할인
 */
@AllArgsConstructor
public class PeriodCondition implements DiscountCondition {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    @Override
    public boolean isSatisfiedBy(Screening screening) {
        final LocalDateTime screeningStartTime = screening.getStartTime();
        return screeningStartTime.getDayOfWeek().equals(dayOfWeek) &&
                startTime.isBefore(screeningStartTime.toLocalTime()) &&
                endTime.isAfter(screeningStartTime.toLocalTime());
    }
}
```

```java
/**
 * 비율 할인
 */
public class PercentDiscountPolicy extends DiscountPolicy {
    private final double percent;

    public PercentDiscountPolicy(double percent, DiscountCondition... conditions) {
        super(conditions);
        this.percent = percent;
    }

    @Override
    protected Money getDiscountAmount(Screening screening) {
        return screening.getMovieFee().times(percent);
    }
}
```

![image](https://github.com/bithumb-study/study-object/assets/58027908/9b3fa641-e33b-4522-b4f1-45597ecd3daa)

### 🔖 2.3.2 할인 정책 구성하기

Movie 는 하나의 할인 정책만 설정 할수 있지만 할인 조건의 경우에는 여러 개를 적용할수 있다.

```java

@Getter
@AllArgsConstructor
public enum MovieDiscountPolicy {
    AVATAR(new AmountDiscountPolicy(Money.wons(800), new SequenceCondition(1), new SequenceCondition(10), new PeriodCondition(DayOfWeek.MONDAY,
            LocalTime.of(10, 0), LocalTime.of(11, 59)))),
    TITANIC(new PercentDiscountPolicy(0.1,
            new PeriodCondition(DayOfWeek.TUESDAY,
                    LocalTime.of(10, 0), LocalTime.of(11, 59)),
            new SequenceCondition(2),
            new PeriodCondition(DayOfWeek.MONDAY,
                    LocalTime.of(10, 0), LocalTime.of(11, 59))
    ));

    private final DiscountPolicy discountPolicy;
}
```

## 📖 2.4 상속과 다형성

### 🔖 2.4.1 컴파일 시간 의존성과 실행 시간 의존성

![image](https://github.com/bithumb-study/study-object/assets/58027908/7e14ad17-7287-4856-be9a-204ba058372c)

movie 클래스는 DiscountPolicy 와 연결되어 있지만 요금 계산하기 위해서는 상속을 받은 클래스가 필요하다.

하지만 코드 수준에서는 movie 는 추상클래스만 의존

코드 상에서 movie는 추상클래스만 의존 하지만 실행 시점에는 상속 받은 자식 클래스를 의존하게 된다.

코드의 의존성과 실행 시점의 의존성은 서로 다를수 있다.

- 클래스 사이의 의존성과 객체 사이의 의존성은 동일하지 않을수 있으며 유연하고 쉽게 재사용 할수 있다.
- 확장 가능한 객체지향 설계가 가지는 특징은 코드의 의존성과 실행 시점의 의존성이 다르다는것

코드 의존성과 실행시점 의존성이 다르면 코드를 이해하기 어려워 진다.

하지만 코드 의존성과 실행시점 의존성이 다르면 코드는 더 유연해지고 확장 가능해 진다.

- 설계가 유연해 질수록 코드를 이해하고 디버깅 하기는 점점 더 어려워 진다.
- 반면 유연성을 억제하면 재사용성과 확장 가능성은 낮아진다.

### 🔖 2.4.2 차이에 의한 프로그래밍

상속은 객체지향에서 코드를 재사용 하기 위해 널리 사용되는 방식

차이에 의한 프로그래밍

- 부모 클래스와 다른 부분만을 추가해서 새로운 클래스를 쉽고 빠르게 만드는 방법

### 🔖 2.4.3 상속과 인터페이스

상속이 가치있는 이유 : 부모 클래스가 제공하는 모든 인터페이스를 자식 클래스가 물려받을 수 있기 때문에

- 결과적으로 외부 객체는 자식 클래스를 부모 클래스와 동일한 타입으로 간주할 수 있다.
- 컴파일러 코드 상에서는 부모 클래스가 나오는 모든 장소에서 자식 클래스를 사용하는 것을 허용한다.

업캐스팅

- 자식클래스가 부모 클래스를 대신 하는것
- ex) 부모: DiscountPolicy 자식: AmountDiscountPolicy

### 🔖 2.4.4 다형성

Movie는 동일한 메시지를 전송하지만 실제로 어떤 메서드가 실행될 것인지는 메시지를 수신하는 객체의 클래스가 무엇이냐에 따라 달라진다.
이를 다형성이라한다.

다형성

- 컴파일 시간 의존성과 실행 시간의 의존성이 다를수 있다는 사실을 기반
- 동일한 메시지를 수신했을 때 객체의 타입에 따라 다르게 응답할 수 있는 능력을 의미

지연 바인딩, 동적 바인딩

- 메시지와 메서드를 실행 시점에 바인딩하는 것

초기 바인딩, 정적 바인딩

- 컴파일 시점에 실행될 함수나 프로시저를 결정하는 것

### 🔖 2.4.5 인터페이스와 다형성

인터페이스

- 구현 공유 필요없이 순수하게 인터페이스만 공유

## 📖 2.5 추상화와 유연성

### 🔖 2.5.1 추상화의 힘

DiscountPolicy , DiscountCondition 은 추상적인 이유는 인터페이스에 초점을 맞추기 때문에

둘다 같은 계층에 속하는 클래스 들이 공통으로 가질 수 있는 인터페이스를 정의 하며
구현의 일부 (추상클래스 DiscountPolicy) 또는 전체 (인터페이스 DiscountCondition)를 자식 클래스가 결정할 수 있도록 결정권을 위임

장점
1. 추상화의 계층만 따로 떼어놓고 살펴보면 요구사항의 정책을 높은 수준에서 서술 할수 있다.
2. 설계가 좀더 유연해진다.

추상화를 사용하면 세부적인 내용을 무시한 채 상위 정책을 쉽고 간단하게 표현가능
- 이것은 기본적인 애플리케이션의 협력 흐름을 기술한다는 것을 의미

재사용 가능한 설계의 기본을 이루는 디자인 패턴이나 프레임워크 모두 추상화를 이용해 정책을 정의하는 객체지향 메커니즘 을 활용

### 🔖 2.5.2 유연한 설계

```java
@AllArgsConstructor
public class Movie {
    private String title;
    private Duration runningTime;
    @Getter
    private Money fee;
    private DiscountPolicy discountPolicy;

    public Money calculateMovieFee(Screening screening) {
        if (discountPolicy == null) {
            return fee;
        }
        return fee.minus(discountPolicy.calculateDiscountAmount(screening));
    }
}
```
할인 정책이 없는 경우는 예외 케이스인데 위의 경우에는 할인정책을 결정하는 책임이 DiscountPolicy 가 아니라
Movie쪽에 있기 때문에 기존에 설계해왔던것을 해치게 된다.

```java
public class NoneDiscountPolicy extends DiscountPolicy{
    @Override
    protected Money getDiscountAmount(Screening screening) {
        return Money.ZERO;
    }
}

```
일관성을 유지시키기 위해 0원 할인정책을 그대로 DiscountPolicy 계층에 유지시킨다.
```java
@Getter
@AllArgsConstructor
public enum MovieDiscountPolicy {
    AVATAR(new AmountDiscountPolicy(Money.wons(800), new SequenceCondition(1), new SequenceCondition(10), new PeriodCondition(DayOfWeek.MONDAY,
            LocalTime.of(10, 0), LocalTime.of(11, 59)))),
    TITANIC(new PercentDiscountPolicy(0.1,
            new PeriodCondition(DayOfWeek.TUESDAY,
                    LocalTime.of(10, 0), LocalTime.of(11, 59)),
            new SequenceCondition(2),
            new PeriodCondition(DayOfWeek.MONDAY,
                    LocalTime.of(10, 0), LocalTime.of(11, 59))
            )),
    STARWARS(new NoneDiscountPolicy())
    ;

    private final DiscountPolicy discountPolicy;
}
```
이처럼 추상화를 중심으로 코드의 구조를 설계하면 유연하고 확장 가능한 설계를 만들수 있다.

추상화가 유연한 설계를 가능하게 하는 이유는 구체적인 상황에 결합되는 것을 방지하기 때문이다.

### 🔖 2.5.3 추상 클래스와 인터페이스 트레이드오프

NoneDiscountPolicy 는 정책을 추가 해도 condition 이 없으면 0 원을 반환한다.

해결

DiscountPolicy 를 인터페이스로 바꾼다.

```java
@FunctionalInterface
public interface DiscountPolicy {
    Money calculateDiscountAmount(Screening screening);
}
```
```java
/**
 * discountPolicy 인터페이스를 상속받는다.
 */
public class NoneDefaultDiscountPolicy implements DiscountPolicy {

    @Override
    public Money calculateDiscountAmount(Screening screening) {
        return Money.ZERO;
    }
}
```
```java

/**
 * DiscountPolicy 인터페이스 추가
 */
public abstract class DefaultDiscountPolicy implements DiscountPolicy {

    private List<DiscountCondition> conditions = new ArrayList<>();

    public DefaultDiscountPolicy(DiscountCondition... conditions) {
        this.conditions = Arrays.asList(conditions);
    }

    public Money calculateDiscountAmount(Screening screening) {
        for (DiscountCondition each : conditions) {
            if (each.isSatisfiedBy(screening)) {
                return getDiscountAmount(screening);
            }
        }
        return Money.ZERO;
    }

    abstract protected Money getDiscountAmount(Screening screening);
}
```
![image](https://github.com/bithumb-study/study-object/assets/58027908/f102b423-20ea-4405-83bc-158c8c86383a)

구현과 관련된 모든 것들이 트레이드 오프 대상이 될수 있다는 사실이다.

### 🔖 2.5.4 코드 재사용

상속은 코드를 재사용하기 위해 널리 사용되는 방법

코드를 재사용하기 위해서는 상속보다는 합성이 더 좋은 방법
- 합성은 다른 객체의 인스턴스를 자신의 인스턴스 변수로 포함해서 재사용하는 방법을 말한다.

### 🔖 2.5.5 상속

상속은 객체 지향에서 코드를 재사용하기 위해 널리 사용되는 기법

두가지 문제점
1. 캡슐화를 위반한다.
   - 부모 클래스의 구현이 자식 클래스에게 노출되기 떄문에 캘슐화가 약화된다.
   - 캡슐화가 약화되면 자식클래스가 부모 클래스에 강하게 결합되어 코드를 변경하기 어려워진다.
2. 설계가 유연하지 않다는 것이다.
   - 부모클래스와 자식 클래스 사이의 관계를 컴파일 시점에 결정하기 때문에 실행 시점에 객체의 종류를 변경하는 것이 불가능하다.
   - 인스턴스 변수로 연결한 기존 방법을 사용하면 실행 시점에 할인 정책을 간단하게 변경가능하다.

```java
@AllArgsConstructor
public class Movie {
    private String title;
    private Duration runningTime;
    @Getter
    private Money fee;
    private DiscountPolicy discountPolicy;

    public Money calculateMovieFee(Screening screening) {
        return fee.minus(discountPolicy.calculateDiscountAmount(screening));
    }

    public void ChangeDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }
}

```

상속보다 인스턴스 변수로 관계를 연결한 원래의 설계가 더유연하다는 사실을 알수 있다.

이방식은 너무나도 유용하기에 특별한 이름으로 불린다.

### 🔖 2.5.6 합성

인터페이스에 정의된 메시지를 통해서만 코드를 재사용하는 방법을 합성

상속이 가지는 두가지 문제를 해결한다.

1. 인터페이스에 정의된 메시지를 통해서만 재사용가능 하기때문에 구현을 효과적으로 캡슐화 한다.
2. 의존하는 인스턴스를 교체하는것이 비교적 쉽기 때문에 설계를 유연하게 만든다.
   - 상속은 클래스를 통해 강하게 결합되는데 비해 합성은 메시지를 통해 느슨하게 결합된다.





























