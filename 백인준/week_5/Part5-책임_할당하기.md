# 📚 5장 책임 할당하기

데이터 중심의 설계는 행동보다 데이터를 먼저 결정하고 협력이라는 문맥을 벗어나 고립된 객체의
초점을 맞춰 캡슐화를 위반 , 결합도 증가, 코드 변경하기 어려워진다.

데이터 중심 설계로 인해 발생하는 문제점을 해결할수 있는 가장 기본적인 방법은 데이터가 아닌
책임에 초점을 맞추는 것이다.

책임 할당 과정은 일종의 트레이드 오프 활동

## 📖 5.1 책임주도 설계를 향해

책임 중심의 설계로 전환하기 위한 두가지 원칙

- 데이터보다 행동을 먼저 결정하라
- 협력이라는 문맥 안에서 책임을 결정하라

### 🔖 5.1.1 데이터보다 행동을 먼저 결정하라

객체에게 중요한 것은 데이터가 아니라 외부에 제공하는 행동

행동이란 곧 객체의 책임을 의미한다.

데이터는 객체가 책임을 수행하는데 필요한 재료를 제공할 뿐

너무 이른시기에 데이터에 초점을 맞추면 객체의 캡슐화가 떨어져 낮은 응집도, 높은결합도, 변경에 취약한
설계다 된다.

이것을 해결하기 위해 가장 기본적인 방법 질문의 순서를 바꾼다.

데이터 중심 설계
 
이 객체가 포함해야 하는 데이터가 무엇인가? -> 데이터를 처리하는데 필요한 오퍼레이션은 무엇인가? 

책임 설계

이 객체가 수행해야 하는 책임은 무엇인가? -> 첵임을 수행하는 데 필요한 데이터는 무엇인가? 
- 즉 객체의 행동,책임 을 결정후 객체의 상태를 결정한다.

### 🔖 5.1.2 협력이라는 문맥 안에서 책임을 결정하라

객체에게 할당된 책임의 품질은 협력에 적합한 정도로 결정

책임은 객체의 입장이 아니라 객체가 참여하는 협력에 적합해야한다.

협력을 시작하는 주체는 메시지 전송자이기 때문에 협력에 적합한 책임이란 메시지 수신자가
아니라 메시지 전송자에게 적합한 책임을 의미한다.

협력에 적합한 책임을 수확하기 위해서는 객체를 결정후에 메시지를 선택하는 것이 아니라 메시지를
결정한 후에 객체를 선택해야 한다.
객체가 메시지를 선택하는 것이 아니라 메시지가 객체를 선택하게 해야한다.

협력이라는 문맥 안에서 메시지에 집중하는 책임중심의 설계는 캡슐화의 원리를 지키기 쉬워 설계의 
응집도가 높고 결합도가 낮으며 변경에 쉽다.

책임 중심의 설계에서는 협력이라는 문맥안에서 객체가 수행해야할 책임에 초점을 맞춘다.

### 🔖 5.1.3 책임주도 설계

- 시스템이 사용자에게 제공해야 하는 기능인 시스템 책임을 파악한다.
- 시스템 책임을 더 작은 책임으로 분할한다.
- 분할된 책임을 수행할 수 있는 적절한 객체 또는 역할을 찾아 책임을 할당한다.
- 객체가 책임을 수행하는 도중 다른 객체의 도움이 필요한 경우 이를 책임질 적절한 객체 또는 역할을 찾는다.
- 해당 객체 또는 역할에게 책임을 할당함으로써 두 객체가 협력하게 된다.

## 📖 5.2 책임 할당을 위한 GRASP 패턴

GRASP (General Responsibility Assignment Software Pattern)
일반적인 책임 할당을 위한 소프트웨어 패턴
- 객체에게 책임을 할당할 떄 지침으로 삼을 수 있는 원칙들의 집합을 패턴 형식으로 정리한것

### 🔖 5.2.1 도메인 개념에서 출발하기
어떤 책임을 할당해야 할 때 가장 먼저 고민해야 하는 유력한 후보는 바로 도메인 개념이다.

<img width="724" alt="image" src="https://github.com/bithumb-study/study-object/assets/58027908/86e47484-e028-4580-80b3-40650bc60234">

설계를 시작하는 단계에서는 개념들의 의미와 관계가 정확하거나 완벽할 필요가 없다.
이 단계에서는 책임을 할당받는 객체들의 종류와 관계에 대한 유용한 정보를 제공 할수 있다면 충분하다.

중요한 것은 설계를 시작하는 것이지 도메인 개념들을 완벽하게 정리하는것이 아니다.

도메인 개념을 정리하는데 너무 많은 시간을 들이지 말고 빠르게 설계와 구현을 진행하라.

### 🔖 5.2.2 정보 전문가에게 책임을 할당하라

책임 주도 설계 방식의 첫단계는 애플리케이션이 제공해야 하는 기능을 애플리케이션의 책임으로 생각하는것 

책임을 애플리케이션에 대해 전송된 메시지로 간주하고 이를 책임질 첫번째 객체를 선택하는 것으로 설계를 시작한다.

사용자에게 제공해야 하는 기능은 영화예매 이므로 이를 책임으로 간주하면 애플리케이션은 영화를 예매할 책임이 있다.

메시지는 메시지를 수신할 객체가 아니라 메시지를 전송할 객체의 의도를 반영해서 결정한다.

첫 질문 메시지를 전송할 객체는 무엇을 원하는가?

메시지를 결정했으므로 두번째 질문은 메시지를 수신할 객체는 누구인가?
- 이 질문에 답하기 위해서는 객체가 상태와 행동을 통합한 캡슐화의 단위라는 사실에 집중
- 객체는 자신의 상태를 스스로 처리하는 자율적인 존재여야 하고 책임과 책임을 수행하는 데 필요한 상태는 동일한 객체안에 존재해야한다.

정보전문가 패턴
- 객체에게 책임을 할당하는것 첫번째 원칙은 책임을 수행할 정보를 알고있는 객체에게 책임을 할당
- 객체가 자신이 소유하고 있는 정보와 관련된 작업을 수행한다는 일반적인 직관을 표현한것
- 정보는 데이터와 다르다.
  - 책임을 수행하는 객체가 정보를 알고 있다고 해서 그정보를 저장하고 있을 필요는 없다.
- 정보 전문가가 데이터를 반드시 저장하고 있을 필요는 없다

p.140 ~ 141 정보전문가 에게 책임을 할당하는 예시

정보전문가 패턴을 따르는 것만으로도 자율성이 높은 객체들로 구성된 협력 공동체를 구축할 가능성이 높아지는것이다.

### 🔖 5.2.3 높은 응집도와 낮은 결합도
설계는 트레이드 오프 활동이다.

영화 예매시스템에서 movie 대신 screening이 discountCondition과 협력하게되면 아래 그림처럼 된다.
<img width="652" alt="image" src="https://github.com/bithumb-study/study-object/assets/58027908/bc5787b4-ddec-42ec-abd0-e2ac9358c154">

우리는 왜 이설계 대신 이전 설계를 선택한것일까?

그이유는 응집도와 결합도에 있다.

높은 응집도와 낮은 결합도는 객체에 책임을 할당할 때 항상 고려해야 하는 기본원리이다.

책임을 할당할수 있는 다양한 대안들이 존재 한다면 응집도와 결합도의 측면에서 더나은 대안을 선택하는 것이 좋다.
다시말해 두협력 패턴에서 높은 응집도와 낮은 결합도를 얻을수 있는 설계를 선택해야한다.

이를 LOW COUPLING(낮은 결합도) 패턴과 HIGH COHESION(응집도) 패턴 이라고 부른다.

LOW COUPLING 패턴
- 설계의 전체적인 결합도가 낮게 유지되도록 책임을 할당하라
- 의존성을 낮추고 변화의 영향을 줄미여 재사용 성을 증가 시킬수 있을까?

movie 와 discountCondition은 이미 결합하고 있기 때문에 screening이 협력할경우
새로운 결합도가 추가된다. 

따라서 movie가 협력하는것이 더좋다.

HIGH COHESION(응집도) 패턴
- 어떻게 복잡성을 관리할수 있는 수준으로 유지할것인가?
- 높은 응집도를 유지할수 있게 책임을 할당하라

screening 의 가장 중요한 책임은 예매를 생성하는것 만약 screening이 discountCondition과 협력하게
되면 계산과 관련된 책임 일부를 떠안아야 할것이다.

이렇게 되면 요금방식이 변경되게 되면 screening도 함께 변경이 이루어져야 한다.
서로다른 이유로 변경되는 책임을 짊어지게 되므로 응집도가 낮아진다.

movie의 주된 책임은 영화 요금 계산이기 떄문에 요금계산에 필요한 할인 조건을 판단하기위해 discountcondition과
협력하는것은 응집도에 해를 끼치지 않는다.

LOW COUPLING(낮은 결합도) 패턴과 HIGH COHESION(응집도) 패턴은 설계를 진행하면서 책임과 협력의
품질을 검토하는데 중요한 평가 기준이다.

### 🔖 5.2.4 창조자에게 객체 생성 책임을 할당하라

영화 예매의 최종 결과물은 Reservation 인스턴스를 생성하는것 

이것은 협력에 참여하는 어떤 객체에서는 해당 인스턴스를 생성할 책임을 할당해야한다는것을 알수있다.

GRASP의 CREATOR(창조자) 패턴은 이같은 경우에 사용할수 있는 책임 할당 패턴으로써 객체를 생성할 책임을 어떤 객체에게
할당할지에 대한 지침을 제공한다.

CREATOR 패턴의 의도는 어떤 방식으로든 생성되는 객체와 연결되거나 관련될 필요가 있는 객체에게 해당 객체를 생성할 책임을 맡기는것
생성될 객체에 대해 잘알고 있거나 그 객체를 사용해야 하는 객체는 어떤 방식으로든 생성될 객체와 연결될것이다. 다시말해 두객체는 결합된다.

이미 결합돼 있는 객체에게 생성 책임을 할당하는 것은 설계의 전체적인 결합도에 영향을 미치지 않는다.
결과적으로 creator 패턴은 이미 존재하는 객체 사이의 관계를 이용하기 때문에 설계가 낮은 결합도를 유지할수 있게 해준다.

## 📖 5.3 구현을 통한 검증

예매하라 메세지를 처리하는 메서드 구현
```java

public class Screening {
    public Reservation reserve(Customer customer, int audienceCount){
        
    }
}

```

책임이 결정됬으므로 필요한 인스턴수 변수 결정

```java
public class Screening {
    //가격을 계산하라는 메세지를 전송해야하기 때문에 영화를 포함
    private Movie movie;
    private int sequence;
    private LocalDateTime whenScreened;

    public Reservation reserve(Customer customer, int audienceCount) {

    }

    //movie에게 가격을 계산하라는 메시지전송하여 가격을 반환받는다.
    private Money calculate(int audienceCount){
        return movie.calculateMovieFee(this).times(audienceCount);
    }
}

```

screening 을 구현하는 과정에서 movie 에게 전송하는 메시지의 시그니처를 calculateMovieFee 로 선언한 사실에 주목.
이 메시지는 수신자인 movie가 아니라 송신자인 screening 의 의도를 표현
- screening 이 movie의 내부 구현에 대한 어떤 지식도 없이 전송할 메시지를 결정했다는것 
- 이처럼 movie 의 구현을 고려하지 않고 필요한 메시지를 결정하면 movie의 내부 구현을 깔끔하게 캡슐화 할수 있다.

이제 screening 과 movie 를 연결하는 것은 메시지 뿐이기 때문에 메시지가 변경되지 않는 한 movie 가 어떤 수정을 하더라도
screening에 영향을 미치지 않는다.

메시지가 객체를 선택 하도록 책임 주도 설계의 방ㅅ기을 따르면 캡슈화와 낮은 결합도라는 목표를 손쉽게 달성 가능

```java
public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition> discountConditionList;

    private MovieType movieType;
    private Money discountAmout;
    private double discountPercent;

    // discountConditon을 순회하면서 만족하는 조건을 찾고 아니면 기본fee를 반환한다
    public Money calculateMovieFee(Screening screening) {
        if (isDiscountable(screening)) {
            return fee.minus(calculateDiscountAmount());
        }
        return fee;
    }

    //movie 는 discountCondition 에게 할인여부를 판단하라는 메세지 전송
    private boolean isDiscountable(Screening screening) {
        return discountConditionList.stream()
                .anyMatch(condition -> condition.isSatisfiedBy(screening));
    }

    private Money calculateDiscountAmount() {
        switch (movieType) {
            case NONE_DISCOUNT:
                return calculateNoneDiscountAmount();
            case PERCENT_DISCOUNT:
                return calculatePercentDiscountAmount();
            case AMOUNT_DISCOUNT:
                return calculateAmountDiscountAmount();
        }
        throw new IllegalStateException();
    }

    private Money calculateAmountDiscountAmount() {
        return discountAmout;
    }

    private Money calculatePercentDiscountAmount() {
        return fee.times(discountPercent);
    }

    private Money calculateNoneDiscountAmount() {
        return Money.ZERO;
    }
}

```

```java

public class DiscountCondition {
    private DiscountConditionType type;
    private int sequence;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;


    public boolean isSatisfiedBy(Screening screening) {
        if (type == DiscountConditionType.PERIOD) {
            return isSatisfiedByPeriod(screening);
        }
        return isSatisfiedBySequence(screening);
    }

    private boolean isSatisfiedByPeriod(Screening screening) {
        return dayOfWeek.equals(screening.getWhenScreened().getDayOfWeek()) &&
                startTime.compareTo(screening.getWhenScreened().toLocalTime()) <= 0 &&
                endTime.compareTo(screening.getWhenScreened().toLocalTime()) >= 0;
    }
    
    private boolean isSatisfiedBySequence(Screening screening){
        return sequence == screening.getSequence();
    }
}

```

### 🔖 5.3.1 DiscountCondition 개선하기

가장 큰문제 점은 변경에 취약한 클래스를 포함하고 있다는것이다.
변경에 취약한 클래스란 코드를 수정해야 하는 이유를 하나 이상 가지는 클래스이다.

DiscountCondtion 세가지 이유로 변경가능

1. 새로운 할인 조건 추가
2. 순번조건을 판단하는 로직 변경
3. 기간 조건을 판단하는 로직이 변경되는 경우

DiscountCondition 은 하나이상의 변경이유를 가지기 떄문에 응집도가 낮다.
응집도가 낮다는 것은 서로 연관성이 없는 기능이나 데이터가 하나의 클래스 안에 뭉쳐져 있다는 것을 의미한다.
따라서 낮은 응집도가 초래하는 문제를 해결하기 위새허는 변경의 이유에 따라 클래스를 분리해야 한다.

코드를 통해 변경의 이유를 파악하는 방법

1. 인스턴스 변수가 초기화 되는 시점을 살펴 보는것
   - 응집도가 높은 클래스는 인스턴스 생성시 모든 속성을 초기화 
   - 응집도가 낮은 클래스는 객체의 속성중 일부만 초기화 일부는 남겨진다.
   - disounctCondition 에서 순번조건을 표현시 sequence는 초기화 되지만 period는 초기화 되지않는다.
   - 함께 초기화 되는 속성을 기준으로 코드를 분리해야 한다.

2. 메서드들이 인스턴스 변수를 사용하는 방식
   - 모든 메서드가 객체의 모든 속성을 사용한다면 클래스의 응집도는 높다고 볼수 있다.
   - 반면 메서드들이 사용하는 속성에 따라 그룹이 나뉜다면 응집도가 낮다고 볼수 있다.
   - isSatisfiedBySequence는  sequence만 사용하고 나머지는 사용하지 않는다.
   - 응집도를 높이기 위해 속성그룹과 해당 그룹에 접근하는 메서드 그룹을 기준으로 코드를 분리해야한다.

### 🔖 5.3.2 타입 분리하기

DiscountCondition의 가장 큰 문제는 순번 조건과 기간조건이라는 두개의 독립적인 타입이 하나의 클래스안에
공존하고 있다는 점이다.

```java
@AllArgsConstructor
public class SequenceCondition {
    private int sequence;

    private boolean isSatisfiedBy(Screening screening){
        return sequence == screening.getSequence();
    }
}
```

```java
//조건별로 분리
@AllArgsConstructor
public class PeriodCondition {

    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    private boolean isSatisfiedBy(Screening screening) {
        return dayOfWeek.equals(screening.getWhenScreened().getDayOfWeek()) &&
                startTime.compareTo(screening.getWhenScreened().toLocalTime()) <= 0 &&
                endTime.compareTo(screening.getWhenScreened().toLocalTime()) >= 0;
    }
    
}
```

클래스를 분리

클래스에 있는 모든 메서드는 동일한 인스턴스 변수를 그룹을 사용 결과적으로 개별 클래스들의 응집도가
향상됐다. 

새로운 문제는 movie 와 협력하는 클래스는 원래 discountCondition 하나였는데 수정후에는 두개의
클래스의 인스턴스와 모두 협력해야한다.

```java

public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<PeriodCondition> periodConditions;
    private List<SequenceCondition> sequenceConditions;

    private MovieType movieType;
    private Money discountAmout;
    private double discountPercent;

    // discountConditon을 순회하면서 만족하는 조건을 찾고 아니면 기본fee를 반환한다
    public Money calculateMovieFee(Screening screening) {
        if (isDiscountable(screening)) {
            return fee.minus(calculateDiscountAmount());
        }
        return fee;
    }

    //movie 는 discountCondition 에게 할인여부를 판단하라는 메세지 전송
    private boolean isDiscountable(Screening screening) {
        return checkPeriodConditions(screening) || checkSequenceConditions(screening);
    }

    private boolean checkPeriodConditions(Screening screening) {
        return periodConditions.stream()
                .anyMatch(condition -> condition.isSatisfiedBy(screening));
    }

    private boolean checkSequenceConditions(Screening screening) {
        return sequenceConditions.stream()
                .anyMatch(condition -> condition.isSatisfiedBy(screening));
    }
}
```
새로운 문제를 야기

1. movie 가 두클래스 모두에게 결합 설계관점에서 결합도가 증가 하였다.
2. 새로운 할인 조건을 추가하기 어려워졌다.

클래스를 분리하기 전에는 DiscountCondition 의 내부구현만 수정하면 되었지만 Movie에는 아무런 영향이 없었다.
하지만 수정후에는 할인조건을 추가하려면 movie도 함께 수정해야한다.

DisounctCondition 입장에서는 응집도는 높아졌지만 변경과 캡슐화라는 관점에서 보면 전체적으로 설계의 품질이 나빠졌다.

### 🔖 5.3.3 다형성을 통해 분리하기

사실 movie 입장에서 보면 두개의 할인조건은 아무차이가 없다.
두가지 모두 할인 여부를 판단하는 동일한 책임을 수행하기 떄문이다.

이 시점이 되면 역할의 개념이 등장

SequneceCondition 과 PeriodCondition이 동일한 책임을 수행한다는것은
동일한 역할을 수행한다는것을 의미한다.

역할의 개념을 적용하면 구체적인 클래스는 알지못하고 오직 역할에만 결합되도록 의존성을 제한할수 있다.

역할을 구현하기위해 인터페이스 혹은 추상클래스를 사용한다.

```java
// 역할
public interface DiscountCondition {
    boolean isSatisfiedBy(Screening screening);
}
```

```java
public class SequenceCondition implements DiscountCondition {...}
public class PeriodCondition implements DiscountCondition{..}
```

```java
public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition> discountConditionList;

    private MovieType movieType;
    private Money discountAmout;
    private double discountPercent;

    // discountConditon을 순회하면서 만족하는 조건을 찾고 아니면 기본fee를 반환한다
    public Money calculateMovieFee(Screening screening) {
        if (isDiscountable(screening)) {
            return fee.minus(calculateDiscountAmount());
        }
        return fee;
    }

    //movie 는 discountCondition 에게 할인여부를 판단하라는 메세지 전송
    private boolean isDiscountable(Screening screening) {
        return discountConditionList.stream().anyMatch(condition -> condition.isSatisfiedBy(screening));
    }
}
```

movie 는 구체적인 타입을 몰라도 협력하는 객체가 DiscountCondition 역할을 수행할수있고 isSatisfiedBy 메시지를 이해할수 있다는 사실만으로 충분

객체의 암시적인 타입에 따라 행동을 분기해야 한다면 암시적인 차입을 명시적인 클래스로 정의하고 행동을 나눔으로써 응집도 문제를 해결할수 있다.

객체의 타입에 따라 변하는 행동이 있다면 타입을 분리하고 변화하는 행동을 각 타입의 책임으로 할당하라는것
이를 POLYMORPHISM (다형성) 패턴이라고 부른다.

### 🔖 5.3.4 변경으로부터 보호하기

DiscountCondition 의 두 서브클래스는 서로 다른 이유로 변경된다는 사실을 알 수있다.

두개의 서로 다른 변경이 두 개의 서로다른 클래스 안으로 캡슐화된다.

새로운 할인조건 추가하는 경우에는 DiscountCondition 이라는 추상화가 구체적인 타입을 캡슐화 한다.

오직 DiscountCondition 인터페이스를 실체화하는 클래스를 추가하는 것으로 할인조건의 종류를 확장가능

이처럼 변경을 캡슐화하도록 책임을 할당 하는것을 GRASP 에서는 PROTECTED VARIATIONS(변경 보호) 패턴이라고 한다.

클래스를 변경에 따라 분리하고 인터페이스를 이용해 변경을 캡슐화 하는 것은 설계의 결합도와 응집도를 향상시키는 매우 강력한 방법

하나의 클래스가 여러타입의 행동을 구현하고 있는 것처럼 보인다면 클래스를 분해하고 POLYMORPHISM 패턴에 따라 책임을 분산

예측 가능한 변경으로 인해 여러 클래스들이 불안정해진다면 PROTECTED VARIATION 패턴에 따라 안정적인 인터페이스 뒤로 변경을 캡슐화하라

### 🔖 5.3.5 MOVIE 클래스 개선하기

Movie 클래스 역시 두가지 할인 정책이 하나의 클래스안에 구현하고 있어 응집도가 낮다. 

POLYMORPHISM 패턴을 사용해 서로다른 행동을 타입별로 분리한다.

```java
public abstract class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondition>  discountConditionList;
    
    public Movie(String title,Duration runningTime,Money fee, DiscountCondition ... discountConditions){
        this.title=title;
        this.runningTime=runningTime;
        this.fee=fee;
        this.discountConditionList = Arrays.asList(discountConditions);
    }

    // discountConditon을 순회하면서 만족하는 조건을 찾고 아니면 기본fee를 반환한다
    public Money calculateMovieFee(Screening screening) {
        if (isDiscountable(screening)) {
            return fee.minus(calculateDiscountAmount());
        }
        return fee;
    }

    //movie 는 discountCondition 에게 할인여부를 판단하라는 메세지 전송
    private boolean isDiscountable(Screening screening) {
        return discountConditionList.stream().anyMatch(condition -> condition.isSatisfiedBy(screening));
    }
    
    protected abstract Money calculateDiscountAmount();
    
}

```

할인 정책에 따라 계산하는 방식이 달라지기 때문에 추상메서드로 분리한다.

```java
public class AmountDiscountMovie extends Movie {
    private Money discountAmount;

    public AmountDiscountMovie(String title, Duration runningTime, Money fee,Money discountAmount, DiscountCondition... discountConditions) {
        super(title, runningTime, fee, discountConditions);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money calculateDiscountAmount() {
        return discountAmount;
    }
}

```

```java
public class PercentDiscountMovie extends Movie {
    private double percent;

    public PercentDiscountMovie(String title, Duration runningTime, double percent, Money fee, DiscountCondition... discountConditions) {
        super(title, runningTime, fee, discountConditions);
        this.percent = percent;
    }

    @Override
    protected Money calculateDiscountAmount() {
        return getFee().times(percent);
    }
}
```

```java
public class NoneDiscountMovie extends Movie{
    public NoneDiscountMovie(String title, Duration runningTime, Money fee, DiscountCondition... discountConditions) {
        super(title, runningTime, fee, discountConditions);
    }

    @Override
    protected Money calculateDiscountAmount() {
        return Money.ZERO;
    }
}

```

모든 클래스의 내부 구현은 캡슐화돼 있고 모든 클래스는 변경의 이유를 오직 하나씩만 가진다.

결론은 데이터가 아닌 책임을 중심으로 설계 하라는 것이다.

객체에게 중요한 것은 상태가 아니라 행동이다.

객체지향 설계의 기본은 책임과 협력에 초점을 맞추는것이다.

### 🔖 5.3.6 변경과 유연성

변경에 대비하는 두가지 방법

1. 코드를 이해하고 수정하기 쉽도록 단순하게 설계
2. 코드를 수정하지 않고도 변경을 수용할수 있도록 유현하게 만드는것

유사한 변경이 반복적으로 발생하면 복잡성이 상승하더라도 유연성을 추가하는 두번째 방법이 낫다.

현재 할인 정책에서는 상속을 이용하기 때문에 새로운 할인 정책이 추가 되면 인스턴스를 생성후 필요정보를
복사해야하므로 변경 전후의 인스턴스가 개념적으로는 동일한 객체지만 물리적으로 다른객체 이기 떄문에 식별자의 관점에서 혼란스럽다.

해결방법은 상속대신 합성을 사용

movie의 상속 계층안에 구현된 할인 정책을 독립적인 DiscountPolicy로 분리후 movie 에 합성시킨다.

<img width="716" alt="image" src="https://github.com/bithumb-study/study-object/assets/58027908/b5d94bf6-3e2c-4872-86e7-829c32989d6a">

## 📖 5.4 책임 주도 설계의 대안

리팩터링

코드를 빠르게 작성후 코드상에 명확하게 드러나는 책임들을 올바른 위치로 이동시키는것

코드를 수정후 겉으로 드러나는 동작이 바뀌지않고 캡슐화를 향상 , 응집도를 높이고, 결합도를 낮춰야한다.

### 🔖 5.4.1 메서드 응집도

긴 메서드는 다양한 측면에서 코드의 유지 보수에 부정적인 영향을 미친다.

- 어떤 일을 수행하는지 한눈에 파악하기 어렵기 때문에 코드를 전체적으로 이해하는데 너무 많은 시간이 걸린다.
- 하나의 메서드 안에서 너무 많은 작업을 처리하기 때문에 변경이 필요할 때 수정해야 할 부분을 찾기 어렵다.
- 메서드 내부의 일부 로직만 수정하더라도 메서드의 나머지 부분에서 버그가 발생할 확률이 높다.
- 로직의 일부만 재사용하는 것이 불가능 하다.
- 코드를 재사용하는 유일한 방법은 원하는 코드를 복사해서 붙여넣는 것뿐이므로 코드 중복을 초래하기 쉽다.

몬스터 메서드라고 부른다.

응집도가 낮은 메서드는 로직의 흐름을 이해하기 위해 주석이 필요한 경우가 대부분이다.

주석을 추가하는 대신 메서드를 작게 분해해서 각 메서드의 응집도를 높여라

객체로 책임을 분배할때 가장 먼저 할 일은 메서드를 응집도 있는 수준으로 분해하는 것이다.

### 🔖 5.4.2 객체를 자율적으로 만들자

자신이 소유하고 있는 데이터를 자기 스스로 처리하도록 만드는것이 자율적인 객체를 만드는 지름길
따라서 메서드가 사용하는 데이터를 저장하고 있는 클래스로 메서드를 이동시키면 된다.

책임주도 설계가 익숙하지 않다면 일단 데이터 중심으로 구현후 이를 리팩터링하더라도 유사한 결과를 얻을수있다.

처음부터 책임주도 설계 방법을 따르는것보단 동작하는 코드를 작성후 이를 리팩터링하는것이 훌류한 결과물을 낳을수 있으며
캡슐화,결합도,응집도를 이해하고 훌륭한 객체지향 원칙을 적용하기 위해 노력한다면 책임 주도 설계방법을 따르지 않더라도 유연하고
깔끔한 코드를 얻을수 있을것이다.









