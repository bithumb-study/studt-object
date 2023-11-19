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








