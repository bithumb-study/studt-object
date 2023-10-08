# 📚 4장 설계 품질과 트레이드 오프

객체지향 설계란 올바른 객체에게 올바른 책임을 할당하면서 낮은 결합도와 응집도를 가진 구조를 창조하는 활동이다.

1. 객체지향 설계의 핵심이 책임이라는 것
2. 책임을 할당하는 작업이 응집도와 결합도 같은 설계 품질과 깊이 연관돼 있다는 것이다.

훌륭한 설계란 합리적인 비용안에서 변경을 수용할 수 있는 구조를 만드는것

- 응집도가 높고 서로 느슨하게 결합돼 있는 요소로 구성된다.

객체의 상태가 아니라 객체의 행동에 초점을 맞추는 것
객체의 책임에 초점을 맞추는 것

## 📖 4.1 데이터 중심의 영화 예매 시스템

객체지향 설계에서는 두 가지 방법을 이용해 시스템을 객체로 분할

1. 상태를 분할의 중심축으로 삼는 방법
    - 데이터 중심 관점에서 객체는 자신이 포함하고 있는 데이터를 조작하는 데 필요한 오퍼레이션을 정의
    - 객체를 독립된 데이터 데이터 덩어리 로 인식
2. 책임을 분할의 중심축으로 삼는 방법
    - 객체는 다른 객체가 요청할 수 있는 오퍼레이션을 위해 필요한 상태를 보관
    - 객체를 협력하는 공동체의 일원으로 인식

책임에 초점을 맞춰야 한다.

객체의 상태는 구현에 속하는데 구현은 불안정 하기 때문에 변하기 쉽다.
   - 데이터를 객체 분할의 중심축으로 삼으면 구현에 관한 세부사항이 객체의 인터페이스에 스며들게 되어 캡슐화의 원칙이 무너지고 설계 변경에 취약해진다.  

객체의 책임은 인터페이스에 속한다.
   - 책임을 드러내는 안정적인 인터페이스 뒤로 책임을 수행하는 데 필요한 상태를 캡슐화함으로써 구현 변경이 외부로 나가는것을 방지하고 변경에 안정적인 설계를 얻을수있다.

### 🔖 4.1.1 데이터를 준비하자

데이터 중심의 설계란 객체 내부에 저장되는 데이터를 기반으로 시스템을 분할하는 방법이다.

```java
public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition> discountConditions;
    private MovieType movieType;
    private Money discountAmout;
    private double discountPercent;
}
```
데이터 중심 movie 클래스 역시 책임 중심의 movie 클래스와 마찬가지로 영화를 표현한 가장 기본적인 정보를 포함

하지만 할인 조건의 목록 , 할인 정책, 할인 금액, 비율 할인 정책이 movie 안에 직접정의

할인 정책은 영화별로 하나 이기 때문에 MovieType 으로 정책을 결정

```java
public enum MovieType {
    AMOUNT_DISCOUNT,
    PERCENT_DISCOUNT,
    NONE_DISCOUNT
}
```
데이터 중심의 접근 방법
- 객체가 포함해야 하는 데이터에 집중

할인 정책을 알기 위해서는 movieType 을 정의 이 타입의 인스턴스를 속성으로 포함시켜 이 값에 따라 어떤 데이터를 사용할지 결정한다.

객체지향의 가장 중요한 원칙은 캡슐화 내부 데이터가 외부로 나가는것을 막아햐 하는데 가장 간단한 방법 접근자와 수정자를 추가 

```java
@Getter
@Setter
public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition> discountConditions;
    private MovieType movieType;
    private Money discountAmout;
    private double discountPercent;
}
```
```java
public enum DiscountConditionType {
    SEQUENCE,
    PERIOD
}
```
```java
@Getter
@Setter
public class DiscountCondition {
    private DiscountConditionType type;
    private int sequence;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
```
할인 조건을 구하기 위해 DiscountConditionType 에 따라 해당 데이터를 사용

```java
@Getter
@Setter
public class Screening {
    private Movie movie;
    private int sequence;
    private LocalDateTime whenScreened;
}
```
```java
@Getter
@Setter
@AllArgsConstructor
public class Reservation {
   private Customer customer;
   private Screening screening;
   private Money fee;
   private int audienceCount;
}
```
```java
@AllArgsConstructor
public class Customer {
    private String name;
    private String id;
}
```
![image](https://github.com/bithumb-study/study-object/assets/58027908/8912b067-4808-4a8d-942c-bb9ef20d4dd1)

```java
public class ReservationAgency {
    public Reservation reserve(Screening screening, Customer customer,
                               int audienceCount) {
        Movie movie = screening.getMovie();

        boolean discountable = false;
        for(DiscountCondition condition : movie.getDiscountConditions()) {
            if (condition.getType() == DiscountConditionType.PERIOD) {
                discountable = screening.getWhenScreened().getDayOfWeek().equals(condition.getDayOfWeek()) &&
                        condition.getStartTime().compareTo(screening.getWhenScreened().toLocalTime()) <= 0 &&
                        condition.getEndTime().compareTo(screening.getWhenScreened().toLocalTime()) >= 0;
            } else {
                discountable = condition.getSequence() == screening.getSequence();
            }

            if (discountable) {
                break;
            }
        }

        Money fee;
        if (discountable) {
            Money discountAmount = Money.ZERO;
            switch(movie.getMovieType()) {
                case AMOUNT_DISCOUNT:
                    discountAmount = movie.getDiscountAmount();
                    break;
                case PERCENT_DISCOUNT:
                    discountAmount = movie.getFee().times(movie.getDiscountPercent());
                    break;
                case NONE_DISCOUNT:
                    discountAmount = Money.ZERO;
                    break;
            }

            fee = movie.getFee().minus(discountAmount).times(audienceCount);
        } else {
            fee = movie.getFee().times(audienceCount);
        }

        return new Reservation(customer, screening, fee, audienceCount);
    }
}
```
reserve 메서드는 크게 두 부분 
1. DiscountCondition 에 대해 루프를 돌면서 할인가능 여부 판단하는 for 문
2. discountable 변수의 값을 체크하고 적절한 할인 정책에 따라 예매요금을 계산 하는 if 문

## 📖 4.2 설계 트레이드오프

### 🔖 4.2.1 캡슐화

상태와 행동을 하나의 객체 안에 모으는 이유는 객체의 내부 구현을 외부로부터 감추기 위해서다.

객체를 사용하면 변경 가능성이 높은 부분은 내부에 숨기고 외부에는 상대적으로 안정적인 부분만 공개하여 변경의 여파를 통제

구현
   - 변경될 가능성이 높은 부분

인터페이스
   - 상대적으로 안정적인 부분 

설계가 필요한 이유는 요구사항이 변경되기 때문이고 캡슐화가 중요한 이유는 불안정한 부분과 안정적인 부분을 분리해서 변경의 
영향을 통제 할수 있기 때문이다.

캡슐화 - 변경가능성이 높은 부분을 객체 내부로 숨기는 추상화 기법
- 변경될 수 있는 어떤 것이라도 캡슐화 , 이것이 바로 객체지향 설계의 핵심 
- 유지보수성이 목표!

### 🔖 4.2.2 응집도와 결합도

응집도
- 모듈에 포함된 내부 요소들이 연관돼 있는 정도를 나타낸다.
- 모듈내의 모든 요소들이 하나의 목적을 위해 긴밀히 협력하면 높은 응집도 , 서로 다른 목적을 가지면 낮은 응집도
- 객체지향의 관점에서는 객체 또는 클래스에 얼마나 관련 높은 책임을 할당했는지를 나타낸다.

결합도
- 의존성의 정도를 나타내며 다른 모듈에 대해 얼마나 많은 지식을 갖고 있는지를 나타내는 척도
- 객체지향의 관점에서 객체 또는 클래스가 협력에 필요한 적절한 수준의 관계만을 유지하고 있는지를 나타낸다.

좋은 설계란 - 높은 응집도와 낮은 결합도를 가진 모듈로 구성된 설계

변경의 관점
- 응집도
  - 변경이 발생할 때 모듈 내부에서 발생하는 변경의 정도 
  - ![image](https://github.com/bithumb-study/study-object/assets/58027908/66c2fcff-6f45-460d-a829-e9fb271f571a)
- 결합도
  - 한 모듈이 변경되기 위해서 다른 모듈의 변경을 요구하는 정도
  - ![image](https://github.com/bithumb-study/study-object/assets/58027908/cb39e273-1617-44b0-94cf-2a0cc9e2377a)

## 📖 4.3 데이터 중심의 영화 예매 시스템의 문제점

데이터 중심의 설계가 가진 대표적 문제점
- 캡슐화 위반
- 높은 결합도
- 낮은 응집도

### 🔖 4.3.1 캡슐화 위반

```java
@Getter
@Setter
public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition> discountConditions;
    private MovieType movieType;
    private Money discountAmout;
    private double discountPercent;
}
```
movie 클래스를 보면 오직 메서드를 통해서만 객체의 내부 상태에 접근 가능하다.

접근자와 수정자 메서드는 객체 내부의 상태에 대한 어떤 정보도 캡슐화 하지 못한다.

설계할 때 협력에 관해 고민하지 않으면 캡슐화를 위반하는 과도한 접근자와 수정자를 가지게 된다.
- 과도하게 의존하는 설계 방식을 "추측에 의한 설계 전략"
- 결과적으로 내부 구현이 퍼블릭 인터페이스에 그대로 노출되어 캡슐화의 원칙을 위반하게 된다.

### 🔖 4.3.2 높은 결합도

객체 내부의 구현이 객체의 인터페이스에 드러난다는 것은 클라이언트가 구현에 강하게 결합된다는 것을 의미

내부구현을 변경했음에도 인터페이스에 의존하는 모든 클라이언트들도 변경해야한다.

```java
public class ReservationAgency {
   public Reservation reserve(Screening screening, Customer customer,
                              int audienceCount) {
       Money fee;
       if (discountable) {
           Money discountAmount = Money.ZERO;
           switch (movie.getMovieType()) {
               case AMOUNT_DISCOUNT:
                   discountAmount = movie.getDiscountAmount();
                   break;
               case PERCENT_DISCOUNT:
                   discountAmount = movie.getFee().times(movie.getDiscountPercent());
                   break;
               case NONE_DISCOUNT:
                   discountAmount = Money.ZERO;
                   break;
           }

           fee = movie.getFee().minus(discountAmount).times(audienceCount);
       } else {
           fee = movie.getFee().times(audienceCount);
       }

       return new Reservation(customer, screening, fee, audienceCount);
   }
}
```

ex) Money 의 fee 타입을 변경시 메서드의 반환 타입, 메서드를 호출하는 ReservationAgency 클래스도 수정해야 한다.

데이터 중심의 설계는 객체의 캡슐화를 약화시키기 때문에 클라이언트가 객체의 구현에 강하게 결합된다.

또 다른 단점은 여러 데이터 객체들을 사용하는 제어 로직이 특정 객체 안에 집중되기 때문에 하나의 제어 객체가 다수의 데이터 객체에 강하게
결합되어 어떤 데이터 객체를 변경하더라도 제어 객체를 함께 변경할 수 밖에 없다.

### 🔖 4.3.3 낮은 응집도

설계문제점
- 변경의 이유가 서로 다른 코드들을 하나의 모듈 안에 뭉쳐 놓았기 때문에 변경과 아무 상관이 없는 코드들이 영향을 받는다.
- 하나의 요구사항 변경을 반영하기 위해 동시에 여러 모듈을 수정해야 한다.
단일 책임의 원칙

## 📖 4.4 자율적인 객체를 향해

### 🔖 4.4.1 캡슐화를 지켜라

객체는 자신이 어떤 데이터를 가지고 있는지를 내부에 캡슐화 하고 외부에 공개해서는 안된다.

객체는 스스로의 상태를 책임져야 하며 외부에서는 인터페이스에 정의된 메서드를 통해서만 상태에 접근할 수 있어야 한다.

```java
@Getter
@Setter
@AllArgsConstructor
public class Rectangle {
    private int left;
    private int top;
    private int right;
    private int bottom;
}

public class AnyClass {

   void anyMethod(Rectangle rectangle, int multiple){
      rectangle.setRight(rectangle.getRight() * multiple);
      rectangle.setBottom(rectangle.getBottom() * multiple);
   }

}
```
1. 코드의 중복이 발생할 확률이 높다.
2. 변경에 취약하다
   - 접근자와 수정자를 통해 인스턴스를 외부에 노출되게 되고 변경시 모두 기존 접근자 메서드를 사용하는 곳을 모두 변경해야한다.

캡슐화를 강화 한다.

```java
@Getter
@Setter
@AllArgsConstructor
public class Rectangle {
    private int left;
    private int top;
    private int right;
    private int bottom;
    
    public void enlarge(int multiple) {
        right *= multiple;
        bottom *= multiple;
    }
}
```
Rectangle 내부에 로직을 캡슐화 하면 위의 두가지 문제를 해결할수 있다.

책임의 이동 - 객체가 자기 스스로 책임진다.

### 🔖 4.4.2 스스로 자신의 데이터를 책임지는 객체

객체는 단순한 데이터 제공자가 아니라, 객체 내부에 저장되는 데이터보다 객체가 협력에 참여하면서
수행할 책임을 정의하는 오퍼레이션이 더 중요하다.

객체 설계시 "이 객체가 어떤 데이터를 포함해야 하는가?"
- 이 객체가 어떤 데이터를 포함해야 하는가?
- 이 객체가 데이터에 대해 수행해야 하는 오퍼레이션은 무엇인가?

두질문 을 조합시 객체의 내부 상태를 저장하는 방식과 저장된 상태에 대해 호출할 수 있는 오퍼레이션 집합을 얻을수 있다.

1. 어떤 데이터를 관리하는지
2. 이 데이터에 대해 수행할 수 있는 오퍼레이션이 무엇인가를 묻는다.

```java
public class DiscountCondition {
    private DiscountConditionType type;
    private int sequence;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    
    public boolean isDiscountable(DayOfWeek dayOfWeek,LocalTime time) throws IllegalAccessException {
        if(type != DiscountConditionType.PERIOD){
            throw new IllegalAccessException();
        }
        return this.dayOfWeek.equals(dayOfWeek)&& !this.startTime.isAfter(time) && !this.endTime.isBefore(time);
    }
    
    public boolean isDiscountable(int sequence) throws IllegalAccessException {
        if(type != DiscountConditionType.SEQUENCE){
            throw new IllegalAccessException();
        }
        return this.sequence==sequence;
    }
}
```
데이터를 처리할 오퍼레이션

할인 조건을 판단하는 isDiscountable 메서드를 생성하여 판단한다.

```java
@Getter
public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition> discountConditions;
    private MovieType movieType;
    private Money discountAmount;
    private double discountPercent;
    
    public Money calculateAmountDiscountFee() throws IllegalAccessException {
        if(movieType != MovieType.AMOUNT_DISCOUNT){
            throw new IllegalAccessException();
        }
        return fee.minus(discountAmount);
    }

    public Money calculatePercentDiscountFee() throws IllegalAccessException {
        if(movieType != MovieType.PERCENT_DISCOUNT){
            throw new IllegalAccessException();
        }
        return fee.minus(fee.times(discountPercent));
    }

    public Money calculateNoneDiscountFee() throws IllegalAccessException {
        if(movieType != MovieType.NONE_DISCOUNT){
            throw new IllegalAccessException();
        }
        return fee;
    }
    
    public boolean isDiscountable(LocalDateTime whenScreened, int sequence) throws IllegalAccessException {
        for(DiscountCondition condition: discountConditions){
            if(condition.getType() == DiscountConditionType.PERIOD){
                if(condition.isDiscountable(whenScreened.getDayOfWeek(),whenScreened.toLocalTime())){
                    return true;
                }
            }
            if(condition.isDiscountable(sequence)){
                return true;
            }
        }
        return false;
    }
}
```

Movie 는 요금을 계산하는 오퍼레이션 calculateDiscountFee 를 생성
할인조건 계산 오퍼레이션 isDiscountable 생성

```java
@Getter
@Setter
public class Screening {
    private Movie movie;
    private int sequence;
    private LocalDateTime whenScreened;
    
    public Money calculateFee(int audienceCount) throws IllegalAccessException {
        switch (movie.getMovieType()){
            case AMOUNT_DISCOUNT -> {
                if(movie.isDiscountable(whenScreened,sequence)) {
                    return movie.calculateAmountDiscountFee().times(audienceCount);
                }
            }
            case PERCENT_DISCOUNT -> {
                if(movie.isDiscountable(whenScreened,sequence)) {
                    return movie.calculatePercentDiscountFee().times(audienceCount);
                }
            }
            case NONE_DISCOUNT -> movie.calculateAmountDiscountFee().times(audienceCount);
            }

        return movie.calculateNoneDiscountFee().times(audienceCount);
    }
}
```
calculateFee 오퍼레이션 생성

```java
public class ReservationAgency {
    public Reservation reserve(Screening screening, Customer customer,
                               int audienceCount) throws IllegalAccessException {
        Money fee = screening.calculateFee(audienceCount);
        return new Reservation(customer,screening,fee,audienceCount);
    }
}
```
![image](https://github.com/bithumb-study/study-object/assets/58027908/b197ddd7-3bc0-4339-998b-e4cd5282eca7)

두번째 설계가 첫 번째 설계보다 내부 구현을 더 면밀하게 캡슐화 하고 있다.

데이터를 처리하는 데 필요한 메서드를 데이터를 가지고 있는 객체 스스로 구현하고 있다.

## 📖 4.5 하지만 여전히 부족하다.

### 🔖 4.5.1 캡슐화 위반

DiscountCondition 의 IsDiscountable
- 어떤 타입의 정보가 인스턴스 변수로 포함돼 있는 사실을 외부에 노출 
- getType 메서들 통해 내부에 DiscountConditionType을 포함하고 있다는 정보를 노출

만약 DiscountCondition 의 속성을 변경해야 하면 isDiscountable 을 수정 하고 해당 메서드를 사용하는 모든곳을 수정

내부 구현 변경이 외부로 퍼져나가는 파급효과는 캡슐화가 부족한 명백한 증거 

### 🔖 4.5.2 높은 결합도

캡슐화 위반으로 DiscountCondition의 내부구현이 외부로 노출 됐기 때문에 Movie와 DiscountCondition의 결합도가 높아진다.

두객체 사이에 결합도가 높을 경우 한객체의 구현을 변경할 때 다른객체에게 변경의 영향이 전파될 확률이 높아진다.

모든문제의 원인은 캡슐화를 지키지 않았기 때문이다.

### 🔖 4.5.3 낮은 응집도 

Screening 에서 할인 조건의 종류를 변경하기 위해서는 DiscountCondition,Movie,Screening 모두 수정 해야한다.

하나의 변경을 수용하기 위해 코드의 여러 곳을 동시에 변경해야 한다는것은 설계의 응집도가 낮은것

## 📖 4.6 데이터 중심의 설계의 문제점

1. 데이터 중심의 설계는 본질적으로 너무 이른 시기에 데이터에 관해 결정하도록 강요
2. 협력이라는 문맥을 고려하지 않고 객체를 고립시킨 채 오퍼레이션을 결정한다.

### 🔖 4.6.1 데이터 중심 설계는 객체의 행동보다는 상태에 초점을 맞춘다.

데이터 중심의 관점에서 객체는 그저 단순한 데이터의 집합체일 뿐

설계가 실패한 이유
1. 과도한 접근자 수정자 추가하여 객체를 사용하는 절차를 분리된 별도의 객체 안에 구현 캡슐화가 무너진다.
2. 객체의 인터페이스는 구현을 캡슐화하는데 실패하고 코드 변경에 취약해진다.

데이터 중심의 설계는 너무 이른시기에 데이터에 대해 고민하기 때문에 캡슐화에 실패하게된다.

객체 내부 구현이 인터페이스를 어지럽히고 객체의 응집도와 결합도에 나쁜 영향을 미쳐 변경에 취약한 코드를 만든다.

### 🔖 4.6.2 데이터 중심의 설계는 객체를 고립시킨 채 오퍼레이션을 정의하도록 만든다.

올바른 객체지향 설계의 무게 중심은 항상 객체의 내부가 아니라 외부에 맞춰져 있어야 한다.

중요한것은 다른 객체와 협력하는 방법

데이터 중심의 설계에서 초점은 외부가 아니라 내부이다.

객체의 구현이 이미 결정된 상태에서 다른 객체와의 협력을 고민하기 때문에 설계에 유연하게 대처하지 못한다.
