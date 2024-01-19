# 📚 14장 일관성 있는 협력

재사용을 위해서는 객체들의 협력 방식을 일관성 있게 만들어야 한다.

일관성

1. 설계에 드는 비용을 감소 시킨다.
2. 코드가 이해하기 쉬워 진다.

## 📖 14.1 핸드폰 과금 시스템 변경하기

### 🔖 14.1.1 기본정책 확장

요금 정책의 수
![image](https://github.com/bithumb-study/study-object/assets/58027908/cb5961a4-4ff1-414e-b002-3bf990549e45)

요금 방식 클래스
![image](https://github.com/bithumb-study/study-object/assets/58027908/895a8666-b46e-4bbc-a6ed-6301d43d0e44)

### 🔖 14.1.2 고정요금,시간대별, 일별, 구간별 방식 구현하기

```java

@AllArgsConstructor
public class FixedFeePolicy extends BasicRatePolicy {
    private Money amount;
    private Duration seconds;

    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times((double) call.getDuration().getSeconds() / seconds.getSeconds());
    }
}
```

시간대별 방식을 구현하는 데 있어 핵심은 규칙에 따라 통화 시간을 분할하는 방법을 결정하는 것이다.

```java
public class FixedFeePolicy extends BasicRatePolicy {
    private Money amount;
    private Duration seconds;

    public FixedFeePolicy(Money amount, Duration seconds) {
        this.amount = amount;
        this.seconds = seconds;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
}

public class DateTimeInterval {
    private LocalDateTime from;
    private LocalDateTime to;

    public static DateTimeInterval of(LocalDateTime from, LocalDateTime to) {
        return new DateTimeInterval(from, to);
    }

    public static DateTimeInterval toMidnight(LocalDateTime from) {
        return new DateTimeInterval(from, LocalDateTime.of(from.toLocalDate(), LocalTime.of(23, 59, 59, 999_999_999)));
    }

    public static DateTimeInterval fromMidnight(LocalDateTime to) {
        return new DateTimeInterval(LocalDateTime.of(to.toLocalDate(), LocalTime.of(0, 0)), to);
    }

    public static DateTimeInterval during(LocalDate date) {
        return new DateTimeInterval(
                LocalDateTime.of(date, LocalTime.of(0, 0)),
                LocalDateTime.of(date, LocalTime.of(23, 59, 59, 999_999_999)));
    }

    private DateTimeInterval(LocalDateTime from, LocalDateTime to) {
        this.from = from;
        this.to = to;
    }

    public Duration duration() {
        return Duration.between(from, to);
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public List<DateTimeInterval> splitByDay() {
        if (days() > 0) {
            return split(days());
        }

        return Arrays.asList(this);
    }

    private long days() {
        return Duration.between(from.toLocalDate().atStartOfDay(), to.toLocalDate().atStartOfDay()).toDays();
    }

    private List<DateTimeInterval> split(long days) {
        List<DateTimeInterval> result = new ArrayList<>();
        addFirstDay(result);
        addMiddleDays(result, days);
        addLastDay(result);
        return result;
    }

    private void addFirstDay(List<DateTimeInterval> result) {
        result.add(DateTimeInterval.toMidnight(from));
    }

    private void addMiddleDays(List<DateTimeInterval> result, long days) {
        for (int loop = 1; loop < days; loop++) {
            result.add(DateTimeInterval.during(from.toLocalDate().plusDays(loop)));
        }
    }

    private void addLastDay(List<DateTimeInterval> result) {
        result.add(DateTimeInterval.fromMidnight(to));
    }

    public String toString() {
        return "[ " + from + " - " + to + " ]";
    }
}


public class Call {
    private DateTimeInterval interval;

    public Call(LocalDateTime from, LocalDateTime to) {
        this.interval = DateTimeInterval.of(from, to);
    }

    public Duration getDuration() {
        return interval.duration();
    }

    public LocalDateTime getFrom() {
        return interval.getFrom();
    }

    public LocalDateTime getTo() {
        return interval.getTo();
    }

    public DateTimeInterval getInterval() {
        return interval;
    }

    public List<DateTimeInterval> splitByDay() {
        return interval.splitByDay();
    }
}

public class TimeOfDayDiscountPolicy extends BasicRatePolicy {
    private List<LocalTime> starts = new ArrayList<LocalTime>();
    private List<LocalTime> ends = new ArrayList<LocalTime>();
    private List<Duration> durations = new ArrayList<Duration>();
    private List<Money> amounts = new ArrayList<Money>();

    @Override
    protected Money calculateCallFee(Call call) {
        Money result = Money.ZERO;
        for (DateTimeInterval interval : call.splitByDay()) {
            for (int loop = 0; loop < starts.size(); loop++) {
                result.plus(amounts.get(loop).times(Duration.between(from(interval, starts.get(loop)),
                        to(interval, ends.get(loop))).getSeconds() / durations.get(loop).getSeconds()));
            }
        }
        return result;
    }

    private LocalTime from(DateTimeInterval interval, LocalTime from) {
        return interval.getFrom().toLocalTime().isBefore(from) ? from : interval.getFrom().toLocalTime();
    }

    private LocalTime to(DateTimeInterval interval, LocalTime to) {
        return interval.getTo().toLocalTime().isAfter(to) ? to : interval.getTo().toLocalTime();
    }
}

public class DayOfWeekDiscountRule {
    private List<DayOfWeek> dayOfWeeks = new ArrayList<>();
    private Duration duration = Duration.ZERO;
    private Money amount = Money.ZERO;

    public DayOfWeekDiscountRule(List<DayOfWeek> dayOfWeeks,
                                 Duration duration, Money amount) {
        this.dayOfWeeks = dayOfWeeks;
        this.duration = duration;
        this.amount = amount;
    }

    public Money calculate(DateTimeInterval interval) {
        if (dayOfWeeks.contains(interval.getFrom().getDayOfWeek())) {
            return amount.times(interval.duration().getSeconds() / duration.getSeconds());
        }

        return Money.ZERO;
    }
}

public class DayOfWeekDiscountPolicy extends BasicRatePolicy {
    private List<DayOfWeekDiscountRule> rules = new ArrayList<>();

    public DayOfWeekDiscountPolicy(List<DayOfWeekDiscountRule> rules) {
        this.rules = rules;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        Money result = Money.ZERO;
        for (DateTimeInterval interval : call.getInterval().splitByDay()) {
            for (DayOfWeekDiscountRule rule : rules) {
                result.plus(rule.calculate(interval));
            }
        }
        return result;
    }
}

public class DurationDiscountRule extends FixedFeePolicy {
    private Duration from;
    private Duration to;

    public DurationDiscountRule(Duration from, Duration to, Money amount, Duration seconds) {
        super(amount, seconds);
        this.from = from;
        this.to = to;
    }

    public Money calculate(Call call) {
        if (call.getDuration().compareTo(to) > 0) {
            return Money.ZERO;
        }

        if (call.getDuration().compareTo(from) < 0) {
            return Money.ZERO;
        }

        // 부모 클래스의 calculateFee(phone)은 Phone 클래스를 파라미터로 받는다.
        // calculateFee(phone)을 재사용하기 위해 데이터를 전달할 용도로 임시 Phone을 만든다.
        Phone phone = new Phone(null);
        phone.call(new Call(call.getFrom().plus(from),
                call.getDuration().compareTo(to) > 0 ? call.getFrom().plus(to) : call.getTo()));

        return super.calculateFee(phone);
    }
}

public class DurationDiscountPolicy extends BasicRatePolicy {
    private List<DurationDiscountRule> rules = new ArrayList<>();

    public DurationDiscountPolicy(List<DurationDiscountRule> rules) {
        this.rules = rules;
    }

    @Override
    protected Money calculateCallFee(Call call) {
        Money result = Money.ZERO;
        for (DurationDiscountRule rule : rules) {
            result.plus(rule.calculate(call));
        }
        return result;
    }
}
```

기본 정책을 구현하는 기존 클래스들과 일관성이 없어 설계가 훌륭하지 않다.

기존의 설계가 어떤 가이드도 제공하지 않기 때문에 새로운 기본 정책을 구현해야 하는 상황에서 개발자가 또다른 방식으로
정책을 구현할 가능성이 높아 일관성이 깨지기 쉽다.

## 📖 14.2 설계에 일관성 부여하기

일관성 있는 설계를 만드는 조언

1. 다양한 설계 경험을 익혀야 한다.
2. 디자인 패턴 학습하고 변경이라는 문맥안에서 디자인 패턴을 적용해 보라는 것이다.

일관성 있는 협력을 위한 지침

- 변하는 개념을 변하지 않는 개념으로부터 분리하라
- 변하는 개념을 캡슐화하라.

지금까지 이 책에서 설명했던 모든 원칙과 개념들 역시 대부분 변경의 캡슐화라는 목표를 향한다.

### 🔖 14.2.1 조건 로직 대 객체 탐색

객체지향에서 변경을 다루는 전통적인 방법은 조건 로직을 객체 사이의 이동으로 바꾸는 것이다.

조건 로직을 객체 사이의 이동으로 대체하기 위해서는 커다란 클래스를 더 작은 클래스들로 분리해야 한다.

- 단일 책임 원칙을 따르도록 클래스를 분리해야 한다는 것이다.

### 🔖 14.2.2 캡슐화 다시 살펴보기

데이터 은닉

- 오직 외부에 공개된 메서드를 통해서만 객체의 내부에 접근할 수 있게 제한함 으로써 객체 내부의 상태 구현을 숨기는 기법을 가리킨다.
- 클래스의 모든 인스턴스 변수는 private 으로 선언해야 하고 오직 해당 클래스의 메서드만이 인스턴스 변수에 접근할 수 있어야 한다.

캡슐화

- 데이터 은닉 이상 단순히 데이터 를 감추는 것이 아니다.
- 변하는 어떤 것이든 감추는 것이다.
- 퍼블릭 인터페이스와 구현을 분리하는 것
    - 자주 변경되는 내부 구현을 안정적인 퍼블릭 인터페이스 뒤로 숨겨야 한다.

1. 변하는 부분을 분리해서 타입 계층을 만든다.
    - 변하지 않는 부분으로부터 변하는 부분을 분리한다.
    - 변하는 부분들의 공통적인 행동을 추상 클래스나 인터페이스로 추상화한후 변하는 부분들이 이 추상 클래스나 인터페이스를 상속받게 만든다.
2. 변하지 않는 부분의 일부로 타입 계층을 합성한다.
    - 앞에서 구현한 타입 계층을 변하지 않는 부분에 합성한다.
    - 변하지 않는 부분에서는 변경되는 구체적인 사항에 결합돼서는 안된다.
    - 의존성 주입과 같이 결합도를 느슨하게 유지할 수 있는 방법을 이용해 오직 추상화에만 의존하게 만든다.

## 📖 14.3 일관성 있는 기본 정책 구현하기

일관성 있는 협력을 만들기 위해 첫 번째 단계는 변하는 개념과 변하지 않는 개념을 분리하는 것이다.

- 변하지 않는 것과 변하는 것을 분리하라는것
- 변하지 않는 규칙으로부터 변하는 적용조건을 분리해야 한다.

### 🔖 14.3.1 변경 캡슐화 하기

협력을 일관성 있게 만들기 위해서는 변경을 캡슐화해서 파급효과를 줄여야 한다.

변경을 캡슐화하는 가장 좋은 방법은 변하지 않는 부분으로부터 변하는 부분을 분리하는 것이다.

변하지 않는 부분이 오직 이 추상화에만 의존하도록 관계를 제한하면 변경을 캡슐화할 수 있게 된다.

### 🔖 14.3.2 협력 패턴 설계하기

```java
public interface FeeCondition {
    List<DateTimeInterval> findTimeIntervals(Call call);
}

public class FeeRule {
    private FeeCondition feeCondition;
    private FeePerDuration feePerDuration;

    public FeeRule(FeeCondition feeCondition, FeePerDuration feePerDuration) {
        this.feeCondition = feeCondition;
        this.feePerDuration = feePerDuration;
    }

    public Money calculateFee(Call call) {
        return feeCondition.findTimeIntervals(call)
                .stream()
                .map(each -> feePerDuration.calculate(each))
                .reduce(Money.ZERO, (first, second) -> first.plus(second));
    }
}

public class FeePerDuration {
    private Money fee;
    private Duration duration;

    public FeePerDuration(Money fee, Duration duration) {
        this.fee = fee;
        this.duration = duration;
    }

    public Money calculate(DateTimeInterval interval) {
        return fee.times(Math.ceil((double) interval.duration().toNanos() / duration.toNanos()));
    }
}

public final class BasicRatePolicy implements RatePolicy {
    private List<FeeRule> feeRules = new ArrayList<>();

    public BasicRatePolicy(FeeRule... feeRules) {
        this.feeRules = Arrays.asList(feeRules);
    }

    @Override
    public Money calculateFee(Phone phone) {
        return phone.getCalls()
                .stream()
                .map(call -> calculate(call))
                .reduce(Money.ZERO, (first, second) -> first.plus(second));
    }

    private Money calculate(Call call) {
        return feeRules
                .stream()
                .map(rule -> rule.calculateFee(call))
                .reduce(Money.ZERO, (first, second) -> first.plus(second));
    }
}

public class TimeOfDayFeeCondition implements FeeCondition {
    private LocalTime from;
    private LocalTime to;

    public TimeOfDayFeeCondition(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public List<DateTimeInterval> findTimeIntervals(Call call) {
        return call.getInterval().splitByDay()
                .stream()
                .filter(each -> from(each).isBefore(to(each)))
                .map(each -> DateTimeInterval.of(
                        LocalDateTime.of(each.getFrom().toLocalDate(), from(each)),
                        LocalDateTime.of(each.getTo().toLocalDate(), to(each))))
                .collect(Collectors.toList());
    }

    private LocalTime from(DateTimeInterval interval) {
        return interval.getFrom().toLocalTime().isBefore(from) ?
                from : interval.getFrom().toLocalTime();
    }

    private LocalTime to(DateTimeInterval interval) {
        return interval.getTo().toLocalTime().isAfter(to) ?
                to : interval.getTo().toLocalTime();
    }
}

public class DayOfWeekFeeCondition implements FeeCondition {
    private List<DayOfWeek> dayOfWeeks = new ArrayList<>();

    public DayOfWeekFeeCondition(DayOfWeek... dayOfWeeks) {
        this.dayOfWeeks = Arrays.asList(dayOfWeeks);
    }

    @Override
    public List<DateTimeInterval> findTimeIntervals(Call call) {
        return call.getInterval()
                .splitByDay()
                .stream()
                .filter(each ->
                        dayOfWeeks.contains(each.getFrom().getDayOfWeek()))
                .collect(Collectors.toList());
    }
}

public class DurationFeeCondition implements FeeCondition {
    private Duration from;
    private Duration to;

    public DurationFeeCondition(Duration from, Duration to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public List<DateTimeInterval> findTimeIntervals(Call call) {
        if (call.getInterval().duration().compareTo(from) < 0) {
            return Collections.emptyList();
        }

        return Arrays.asList(DateTimeInterval.of(
                call.getInterval().getFrom().plus(from),
                call.getInterval().duration().compareTo(to) > 0 ?
                        call.getInterval().getFrom().plus(to) :
                        call.getInterval().getTo()));
    }
}
```

변경을 캡슐화해서 협력을 일관성 있게 만든 장점

1. 변하지 않는 부분을 재사용가능
2. 새로운 기능을 추가하기 위해 오직 변하는 부분만 구현하면 되기 때문에 원하는 기능을 쉽게 완성
3. 코드의 재사용성이 향상되고 테스트해야 하는 코드의 양이 감소한다.
4. 기능을 추가할때 따라야 하는 구조를 강제할 수 있기 때문에 기능을 추가하거나 변경할 때도 설계의 일관성이 무너지지 않는다.

기본 정책을 추가하기 위해 규칙을 지키는 것보다 어기는 것이 더 어렵다.

공통 코드의 구조와 협력 패턴은 모든 기본 정책에 걸쳐 동일하기 때문에 코드를 한번 이해하면 이 지식을 다른 코드를 이해하는데 그대로 적용 할수 있다.

유사한 기능에 대해 유사한 협력 패턴을 적용하는 것은 객체지향 시스템에서 개념적 무결성을 유지할 수 있는 가장 효과적인 방법이다.

### 🔖 14.3.3 협력 패턴에 맞추기

#### 지속적으로 개선하라

- 처음에는 일관성을 유지하는 것처럼 보이던 협력 패턴이 시간이 흐르면서 새로운 요구사항이 추가되는 과정에서 일관성의 벽에 조금씩 금이 가는 경우를 자주 보게된다.
- 협력을 설계하는 초기 단계에서 모든 요구사항을 미리 예상할 수 없기 떄문에 이것은 잘못이 아니면 꽤나 자연스러운 현상이다.
- 오히려 새로운 요구사항을 수용할  수 있는 협력 패턴을 향해 설계를 진화시킬 수 있는 좋은 신호로 받아들여야 한다.
- 협력은 고정된것이 아니다. 만약 현재의 협력 패턴이 변경의 무게를 지탱하기 어렵다면 변경을 수용할 수 있는 협력패턴을 향해 과감하게 리팩터링 하라
- 요구사항의 변경에 따라 협력 역시 지속적으로 개선해야한다.
- 중요한것은 현재의 설계에 맹목적으로 일관성을 맞추는 것이 아니라 달라지는 변경의 방향에 맞춰 지속적으로 코드를 개선하려는 의지이다.

### 🔖 14.3.4 패턴을 찾아라

협력의 핵심은 변경을 분리하고 캡슐화하는 것이다.

애플리케이션에서 유사한 기능에 대한 변경이 지속적으로 발생하고 있다면 변경을 캡슐화할 수 있는 적잘한 추상화를 찾은후 이 추상화에 변하지 않는 공통적인 책임을 할당하라

현재의 구조가 변경을 캡슐화하기에 적합하지 않다면 코드를 수정하지 않고도 원하는 변경을 수용할 수 있도록 협력과 코드를 리팩터링 하라.

협력을 일관성있게 만든다는 것은 유사한 변경을 수용할 수 있는 협력 패턴을 발견하는것과 동일하다.

협력 패턴관 관련해서 언급할 가치가 있는 두가지 개념
1. 패턴
2. 프레임워크 