# 📚 10장 상속과 코드 재사용

객체지향 프로그래밍 장점중 하나는 코드를 재사용하기 용이하다. 코드를 재사용하기 위해 새로운 코드를 추가한다.

상속 - 클래스를 재사용하기 위해 새로운 클래스를 추가하는 기법

합성 - 새로운 클래스 인스턴스 안에 기존 클래스의 인스턴스를 포함시키는 방법

## 📖 10.1 상속과 중복코드

### 🔖 10.1.1 DRY 원칙

중복 코드

- 변경을 방해한다.
- 코드를 수정하는데 필요한 노력을 몇배로 증가시킨다.

중복 여부를 판단하는 기준은 변경이다.

- 요구 사항이 변경되었을 때 두 코드를 함께 수정해야 한다면 이 코드는 중복이다.

신뢰할수 있고 수정하기 쉬운 코드를 만드는 효과적인 방법중 하나는 중복을 제거 하는것이다.

DRY 원칙 - (반복하지마라) 를 따라야 한다.

- 모든 지식은 시스템내에서 단일하고 , 애매하지 않고, 정말로 믿을 만한 표현 양식을 가져야 한다.
- 한번 단 한번(once and only once) 원칙 또는 단일 지점 제어(single point control) 원칙 이라고 불린다.

### 🔖 10.1.2 중복 과 변경

#### 중복 코드 살펴보기 ex) 휴대 전화 요금 어플리케이션

```java
// 개별 통화 기간 저장 
@AllArgsConstructor
public class Call {
    @Getter
    private LocalDateTime from;
    private LocalDateTime to;

    public Duration getDuration() {
        return Duration.between(from, to);
    }
}

@Getter
@AllArgsConstructor
public class Phone {
    private Money amount; // 단위 요금저장
    private Duration seconds; //단위 시간저장
    private List<Call> calls = new ArrayList<>();

    public void call(Call call) {
        this.calls.add(call);
    }

    //통화 요금 계산
    public Money calculateFee() {
        Money result = Money.ZERO;

        for (Call call : calls) {
            result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
        }
        return result;
    }

}

public class StudyApplication {

    public static void main(String... args) {
        Phone phone = new Phone(Money.wons(5), Duration.ofSeconds(10));
        phone.call(new Call(LocalDateTime.of(2018, 1, 1, 12, 10, 0),
                LocalDateTime.of(2018, 1, 1, 12, 11, 0)));

        phone.call(new Call(LocalDateTime.of(2018, 1, 2, 12, 10, 0),
                LocalDateTime.of(2018, 1, 2, 12, 11, 0)));

        phone.calculateFee();
    }

}
```

심야 할인 요금제라는 새로운 요구 사항 추가

이 요구 사항을 쉽게 해결할 수 있는 가장 쉽고도 빠른 방법은 phone 코드를 복사해 만드는 것이다.

```java

@Getter
@AllArgsConstructor
public class NightlyDiscountPhone {
    private static final int LATE_NIGHT_HOUR = 22;

    private Money nightlyAmount; // 단위 요금저장
    private Money regularAmount; // 단위 요금저장

    private Duration seconds; //단위 시간저장
    private List<Call> calls = new ArrayList<>();

    public void call(Call call) {
        this.calls.add(call);
    }

    //통화 요금 계산
    public Money calculateFee() {
        Money result = Money.ZERO;

        for (Call call : calls) {
            if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
                result = result.plus(nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
            }
            result = result.plus(regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
        }
        return result;
    }

}
```

phone 의 요구 사항과 구현체가 거의 동일

#### 중복 코드 수정하기

중복코드 가 수정코드에 미치는 영향을 보기 위해 세율 추가

계산 로직이 두코드 모두 들어가 있기 때문에 두 클래스를 모두 수정해야 한다.

중복 코드

- 항상 함꼐 수정돼야 하기 때문에 수정할때 하나라도 빠트린다면 버그로 이어질 것이다.
- 새로운 중복 코드를 부른다.
    - 중복 코드를 제거하지 않은 상태에서 코드를 수정할 수 있는 유일한 방법은 새로운 중복 코드를 추가하는 것뿐이다.
- 중복 코듣가 늘어날 수록 애플리케이션은 변경에 취약해지고 버그 발생 가능성이 높아진다.

민첩하게 변경하기 위해서는 중복 코드를 추가하는 대신 제거해야한다.

기회가 생길 때마다 코드를 DRY하게 만들어야 한다.

#### 타입 코드 사용하기

두 클래스 사이의 중복 코드를 제거하는 한 가지 방법은 클래스를 하나로 합치는 것이다.

타입 코드를 추가하고 타입 코드에 따라 로직을 분기 시키면 코드를 하나로 합칠수 있다.

하지만 타입 코드를 사용하는 클래스는 낮은 응집도와 높은 결합도라는 문제에 시달리게 된다.

```java

@AllArgsConstructor
public class PhoneWithTypeCode {
    private static final int LATE_NIGHT_HOUR = 22;

    enum PhoneType {REGULAR, NIGHTLY}

    private PhoneType type;

    private Money amount; // 단위 요금저장

    private Money nightlyAmount; // 단위 요금저장
    private Money regularAmount; // 단위 요금저장

    private Duration seconds; //단위 시간저장
    private List<Call> calls = new ArrayList<>();

    public PhoneWithTypeCode(Money amount, Duration seconds) {
        this(REGULAR, amount, Money.ZERO, Money.ZERO, seconds);
    }

    public PhoneWithTypeCode(Money nightlyAmount, Money regularAmount, Duration seconds) {
        this(NIGHTLY, Money.ZERO, nightlyAmount, regularAmount, seconds);
    }


    public Money calculateFee() {
        Money result = Money.ZERO;

        for (Call call : calls) {
            if (type == REGULAR) {
                result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
            } else {
                if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
                    result = result.plus(nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
                }
                result = result.plus(regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
            }
        }
        return result;
    }
}
```

객체지향 언어는 타입코드를 사용하지 않고 중복 코드를 관리할 수 있는 효과적인 방법을 제공한다.

상속 - 객체지향 프로그래밍을 대표하는 기법

### 🔖 10.1.3 상속을 이용해서 중복 코드 제거하기

이미 존재하는 클래스와 유사한 클래스가 필요하다면 코드를 복사하지 말고 상속을 이용해 코드를 재사용 하는것

```java
// Phone  을 상속 받고 10시 이전 금액을 구한뒤 10시 이후 금액을 차감하는 방식으로 구현
public class NightlyDiscountPhone extends Phone {
    private static final int LATE_NIGHT_HOUR = 22;
    private Money nightlyAmount;

    public NightlyDiscountPhone(Money amount, Duration seconds, List<Call> calls) {
        super(amount, seconds, calls);
    }

    @Override
    public Money calculateFee() {
        Money result = super.calculateFee();

        Money nightlyFee = Money.ZERO;
        for (Call call : getCalls()) {
            if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
                nightlyFee = nightlyFee.plus(getAmount().minus(nightlyAmount).times((double) call.getDuration().getSeconds() / getSeconds().getSeconds()));
            }
        }
        return result.minus(nightlyFee);
    }

}
```

이 예제를 통해 알수 있는 것처럼 상속을 염두에 두고 설계되지 않은 클래스를 상속을 이용해 재사용하는 것은 생각처럼 쉽지 않다.

요구사항과 구현 사이의 차이가 크면 클수록 코드를 이해하기 어려워 진다. 잘못된 상속은 이 차이를 더 크게 벌린다.

또 이 예제 처럼 상속을 이용해 코드를 재사용 하기 위해서는 부모 클래스의 개발자가 세웠던 가정이나 추론과정을 정확히 이해해야 하며
이것은 자식 클래스의 작성자가 부모 클래스의 구현 방법에 대한 정확한 지식을 가져야 한다는 것을 의미 한다.

따라서 상속은 결합도를 높이고 부모 클래스와 자식 클래스 사이의 강한 결합이 코드를 수정하기 어렵게 만든다.

### 🔖 10.1.4 강하게 결합된 Phone 과 nightlyDiscountPhone

```java

@Getter
@AllArgsConstructor
public class Phone {
    private Money amount; // 단위 요금저장
    private Duration seconds; //단위 시간저장
    private List<Call> calls = new ArrayList<>();

    //세금 추가
    private double taxRate;

    public void call(Call call) {
        this.calls.add(call);
    }

    //통화 요금 계산
    public Money calculateFee() {
        Money result = Money.ZERO;

        for (Call call : calls) {
            result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds())).times(taxRate);
        }
        return result;
    }

}

public class NightlyDiscountPhone extends Phone {
    private static final int LATE_NIGHT_HOUR = 22;
    private Money nightlyAmount;

    public NightlyDiscountPhone(Money amount, Duration seconds, List<Call> calls, double taxRate) {
        super(amount, seconds, calls, taxRate);
    }


    @Override
    public Money calculateFee() {
        Money result = super.calculateFee();

        Money nightlyFee = Money.ZERO;
        for (Call call : getCalls()) {
            if (call.getFrom().getHour() >= LATE_NIGHT_HOUR) {
                nightlyFee = nightlyFee.plus(getAmount().minus(nightlyAmount).times((double) call.getDuration().getSeconds() / getSeconds().getSeconds()));
            }
        }
        return result.minus(nightlyFee).times(getTaxRate());
    }

}
```

위 예처럼 세율을 부모클래스에 추가 하게 되면 자식 클래스에도 똑같이 추가 해줘야 한다.

코드 중복을 제거하기 위해 상속을 사용했음에도 세금을 계산하는 로직을 추가하기 위해 새로운 중복 코드를 만들어야 한다.

이것은 자식클래스가 부모클래스의 구현에 너무 강하게 결합돼 있기 때문에 발생하는 문제다.

상속을 위한 결고 1

- 자식 클래스의 메서드 안에서 super 참조를 이용해 부모 클래스의 메서드를 직접 호출할 경우 두 클래스는 강하게 결합된다.
- super 호출을 제거할 수 있는 방법을 찾아 결합도를 제거하라

이처럼 상속 관계로 연결된 자식 클래스가 부모 클래스의 변경에 취약해지는 현상을 가리켜 취약한 기반 클래스 문제 라고 부른다.
취약한 기반 클래스 문제는 코드 재사용을 목적으로 상속을 사용할 때 발생하는 가장 대표적인 문제다.

## 📖 10.2 취약한 기반 클래스 문제

취약한 기반 클래스 문제 - 부모 클래스의 변경에 의해 자식 클래스가 영향을 받는 현상

- 핵심적인 기반 클래스에 대한 단순한 변경이 전체 프로그램을 불안정한 상태로 만들어 버릴수 있다.
- 상속은 자식 클래스를 점진적으로 추가해서 기능을 확장하는 데는 용이하지만 높은 결합도로 인해 부모 클래스를 점진적으로 개선하는 것은 어렵게 만든다.
- 자식 클래스가 부모 클래스의 구현 세부사항에 의존하도록 만들기 때문에 캡슐화를 약화 시킨다.
- 부모 클래스의 퍼블릭 인터페이스가 아닌 구현을 변경하더라도 자식 클래스가 영향을 받기 쉬워진다.
- 코드의 재사용을 위해 캡슐화의 장점을 희석 시키고 궇녀에 대한 결합도를 높임으로써 객체지향이 가진 강력함을 반감시킨다.

### 🔖 10.2.1 불필요한 인터페이스 상속 문제

자바 초기 버전에서 상속을 잘못 사용한 대표적인 사례 java.util.properties 와 java.util.stack

두 클래스의 공통점은 부모 클래스에서 상속받은 메서드를 사용할 경우 자식 클래스의 규칙이 위반 될 수 있다는것이다.
![image](https://github.com/bithumb-study/study-object/assets/58027908/bbfcf0f8-76aa-4acf-95d3-aa48d5b08f8b)

위의 예처럼 스택은 나중에 추가된 요소가 가장 먼저 추출 되는 LIFO 이지만 vector 를 상속받아 임의의 위치에 요소를 추가하거나 조회 할수 있다.

```java
public class Example {
    public static void main() {
        Stack<String> stack = new Stack<>();
        stack.push("1st");
        stack.push("2nd");
        stack.push("3rd");
        stack.add(0, "4th");

        assertEquals("4th", stack.pop()); // 에러
    }
}
```

문제의 원인은 stack 이 규칙을 무너뜨릴 여지가 있는 위험함 vector 의 퍼블릭 인터페이스까지 상속받았기 때문이다.

인터페이스 설계는 제대로 쓰기엔 쉽게, 엉터리로 쓰기엔 어렵게 만들어야 한다.

객체지향의 핵심은 객체들의 협력이다. 단순히 코드를 재사용하기 위해 불필요한 오퍼레이션이 인터페이스에 스며들도록 방치해서는 안된다.

상속을 위한 경고2

- 상속받은 부모 클래스의 메서드가 자식 클래스의 내부 구조에 대한 규칙을 깨트릴 수 있다.

### 🔖 10.2.2 메서드 오버라이딩의 오작용 문제

```java
public class InstrumentedHashSet<E> extends HashSet<e> {
    private int addCount = 0;

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public static void main() {
        InstrumentedHashSet<String> languages = new InstrumentedHashSet<String>();
        languages.addAll(Arrays.asList("JAVA", "RUBY", "SCALA"));
        // 3 이 아니라 6이 add 된다. 
        // hashset 내부에서 addAll 이 add 를 호출하기 때문에
    }
}
```

상속을 위한 경고3

- 자식 클래스가 부모 클래스의 메서드를 오버라이딩할 경우 부모 클래스가 자신의 메서드를 사용하는 방법에 자식 클래스가 결합될수 있다.

클래스가 상속되기를 원한다면 상속을 위해 클래스를 설계하고 문서화 해야 하며 그렇지 않은 경우에는 상속을 금지 시켜야 한다.

- 내부구현을 공개하고 문서화 하는것은 캡슐화를 어긴다.

상속은 코드 재사용을 위해 캡슐화를 희생한다.

### 🔖 10.2.3 부모 클래스와 자식 클래스의 동시 수정 문제

상속은 기본적으로 부모 클래스의 구현을 재사용한다는 기본 전제를 따르기 때문에 자식 클래스가 부모 클래스의 내부에 대해 속속들이 알도록
강요한다. 따라서 코드 재사용을 위한 상속은 부모 클래스와 자식 클래스를 강하게 결합시키기 떄문에 함께 수정해야 하는 상황 역시
빈번하게 발생할 수밖에 없는 것이다.

상속을 위한 경고4

- 클래스를 상속하면 결합도로 인해 자식 클래스와 부모 클래스의 구현을 영원히 변경하지 않거나, 자식 클래스와 부모 클래스를 동시에 변경하거나
  둘 중 하나를 선택할 수 밖에 없다.

## 📖 10.3 Phone 다시 살펴보기

### 🔖 10.3.1 추상화에 의존하자

NightlyPhone 클래스의 가장 큰 문제점은 Phone 에 강하게 결합되어 있기 때문에 변경시 같이 변경되어야 한다.

이 문제를 해결하는 가장 일반적인 방법은 자식 클래스가 부모 클래스의 구현이 아닌 추상화에 의존하도록 만드는 것이다.
다시 말해 부모 클래스, 자식 클래스 모두 추상화에 의존해야 한다.

- 두 메서드가 유사하게 보인다면 차이점을 메서드로 추출하라. 메서드 추출을 통해 두 메서드를 동일한 형태도 보이도록 만들수 있다.
- 부모 클래스의 코드를 하위로 내리지 말고 자식 클래스의 코드를 상위로 올려라. 부모 클래스의 구체적인 메서드를 자식 클래스로 내리는 것보다
  자식 클래스의 추상적인 메서드를 부모 클래스로 올리는 것이 재사용성과 응집도 측면에서 더 뛰어나 결과를 얻을 수 있다.

### 🔖 10.3.2 차이를 메서드로 추출하라

```java

@Getter
@AllArgsConstructor
public class Phone {
    private Money amount; // 단위 요금저장
    private Duration seconds; //단위 시간저장
    private List<Call> calls = new ArrayList<>();

    //세금 추가

    public void call(Call call) {
        this.calls.add(call);
    }

    //통화 요금 계산
    public Money calculateFee() {
        Money result = Money.ZERO;

        for (Call call : calls) {
            result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds())).times(taxRate);
        }
        return result;
    }

    // 계산 로직 추출
    public Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }

}

public class NightlyDiscountPhone {
    private static final int LATE_NIGHT_HOUR = 22;
    private Duration seconds; //단위 시간저장

    private Money nightlyAmount;
    private Money regularAmount;


    public Money calculateFee() {
        Money result = Money.ZERO;

        for (Call call : calls) {
            result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds())).times(taxRate);
        }
        return result;
    }

    private Money calculateCallFee(Call call) {
        if (call.getFrom().getHour() > LATE_NIGHT_HOUR) {
            return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
        return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }


}
```

calculateFee 메서드는 동일해 졌다.

### 🔖 10.3.3 중복코드를 부모 클래스로 올려라

```java
public abstract class AbstractPhone {
    // 공통 부분을 이동

    private List<Call> calls = new ArrayList<>();


    // 공통 부분을 이동
    public Money calculateFee() {
        Money result = Money.ZERO;

        for (Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }
        return result;
    }

    abstract protected Money calculateCallFee(Call call);
}

public class NightlyDiscountPhone extends AbstractPhone {
    private static final int LATE_NIGHT_HOUR = 22;
    private Duration seconds; //단위 시간저장

    private Money nightlyAmount;
    private Money regularAmount;

    @Override
    protected Money calculateCallFee(Call call) {
        if (call.getFrom().getHour() > LATE_NIGHT_HOUR) {
            return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
        return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
}

@Getter
@AllArgsConstructor
public class Phone extends AbstractPhone {
    private Money amount; // 단위 요금저장
    private Duration seconds; //단위 시간저장
    private List<Call> calls = new ArrayList<>();

    //세금 추가

    public void call(Call call) {
        this.calls.add(call);
    }

    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }

}
```
자식 클래스들 사이의 공통점을 부모 클래스로 옮김으로써 실제 코드를 기반으로 상속 계층을 구성할 수있다.

위로 올리기에서 실수하더라도 추상화할 코드는 눈에띄고 결국 상위 클래스로 올려지면서 코드의 품질은 높아진다.

### 🔖 10.3.4 추상화가 핵심이다.

공통 코드를 이동시킨 후에 각 클래스는 서로 다른 변경의 이유를 가진다는 것에 주목

AbstractPhone 은 전체 통화목록 바뀔때만 변경 Phone 은 일반요금제의 통화 한건을 계산하는 방식이 바뀌면 변경
NightlyDiscountPhone 은 심야 요금제 가 바뀔때만 변경.

세클래스는 각각 하나의 변경 이유만을 가지므로 이 클래스들은 책임 원칙을 준수 하기 때문에 응집도가 높다.

설계 변경 전에는 자식 클래스가 부모 클래스의 구현에 강하게 결합되어 부모 클래스 구현을 변경시 자식 클래스도 함께 영향을 
받았지만 변경후에는 부모 클래스인 AbstractPhone 의 구체적인 구현에 의존하지 않는다. 오직 추상화 에만 의존한다.

메서드의 시그니처가 변경되지 않는 한 부모 클래스의 내부 구현이 변경되더라도 자식 클래스는 영향을 받지 않는다. 

이 설계는 낮은 결합도를 가진다.

의존성 역전 원칙도 준수한다. - 부모 클래스도 자신의 내부에 구현된 추상 메서드를 호출 하기 때문에 

새로운 요금제 추가도 새로운 클래스만 만들어 추가하기만 하면 된다.

지금의 설계는 확장에는 열려있고 수정에는 닫혀 있기 때문에 개방-폐쇄 원칙 역시 준수 한다.

상속 계층이 코드를 진화시키는 데 걸림돌이 된다면 추상화를 찾아내고 상속 계층 안의 클래스들이 그 추상화에 의존하도록 코드를 
리팩터링 하라. 차이점을 메서드로 추출하고 공통적인 부분은 부모 클래스로 이동하라.

### 🔖 10.3.5 의도를 드러내는 이름 선택하기

한가지 아쉬운 점은 바로 클래스의 이름과 관련된 부분이다.

Phone AbstractPhone 의 이름이 명시적이지 않는다는것 

```java
public abstract class Phone{...} // 휴대전화 라는 포괄적인 명시
public class RegularPhone extends Phone{...} // 일반요금제 라는 명시적인 이름 사용
```
좋은 상속 계층을 구성하기 위해서는 상속 계층 안에 속한 클래스들이 구현이 아닌 추상화에 의존해야 한다는 사실

### 🔖 10.3.6 세금 추가하기

```java
public abstract class Phone {
    // 공통 부분을 이동

    private List<Call> calls = new ArrayList<>();
    private double taxRate;


    // 공통 부분을 이동
    public Money calculateFee() {
        Money result = Money.ZERO;

        for (Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }
        return result.plus(result.times(taxRate));
    }

    abstract protected Money calculateCallFee(Call call);
}
```
자식 클래스도 부모 클래스의 인스턴스 변수를 초기화 하기 위해 생성자에 taxRate 를 초기화 해야한다.
클래스라는 도구는 메서드 뿐만 아니라 인스턴스 변수도 함께 포함된다.

-> 객체 생성 로직에 대한 변경을 막기 보다는 핵심 로직의 중복을 막아라.
핵심 로직은 한 곳에 모아 놓고 조심스럽게 캡슐화해야 한다. 그리고 공통 로직은 최대한 추상화 해야 한다.

## 📖 10.4 차이에 의한 프로그래밍

상속을 사용하면 이미 존재하는 클래스의 코드를 기반으로 다른 부분을 구현함으로써 새로운 기능을 쉽고 빠르게 추가 할수 있다.

차이에 의한 프로그래밍 - 기존코드와 다른 부분만을 추가함으로써 애플리케이션의 기능을 확장하는 방법
- 중복 코드를 제거하고 코드를 재사용하는것 

재사용 가능한 코드란 심각한 버그가 존재하지 않는 코드이다.
- 코드의 품질은 유지하면서도 코드를 작성하는 노력과 테스트는 줄일수 있다.

상속을 사용하면
1. 여러 클래스 사이에서 재사용 가능한 코드를 하나의 클래스 안으로 모을 수있다.
2. 새로운 기능을 추가하기 위해 직접 구현해야 하는 코드의 양을 최소화할 수 있다.

하지만 상속의 오용과 남용은 애플리케이션을 이해하고 확장하기 어렵게 만든다.

객체지향에 능숙한 개발자들은 상속의 단점을 피하면서도 코드를 재사용할 수 있는 더 좋은 방법이 있다. 
-> 바로 합성이다.