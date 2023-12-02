# 📚 11장 합성과 유연한 설계

합성
- 전체를 표현하는 객체가 부분을 표현하는 객체를 포함해서 부분 객체의 코드를 재사용한다.
- 두 객체 사이의 의존성은 런타임에 해결된다.
- has-a 관계라고 부른다.
- 구현에 의존하지 않는다.
  - 내부에 포함되는 객체의 구현이 아닌 퍼블릭 인터페이스에 의존한다.
  - 따라서 내부에 포함된 객체의 구현이 변경되더라도 영향을 최소화할수 있어 안정적이다.
- 객체 사이의 동적인 관계이다.
  - 실행 시점에 동적으로 변경이 가능하기 때문이다.
- 내부에 포함되는 퍼블릭 인터페이스를 재사용 한다.
  - 따라서 객체 사이의 낮은 결합도로 대체할 수 있다.

## 📖 11.1 상속을 합성으로 변경하기

상속 남용시 세가지 문제

1. 불필요한 인터페이스 상속 문제
   - 자식 클래스에게는 부적합한 부모 클래스의 오퍼레이션이 상속되기 때문에 자식 클래스 인스턴스의 상태가 불안정해지는 문제
2. 메서드 오버라이딩 오작용 문제
   - 자식 클래스가 부모 클래스의 메서드를 오버라이딩할 때 자식 클래스가 부모 클래스의 메서드 호출 방법에 영향을 받는 문제
3. 부모 클래스와 자식 클래스의 동시 수정 문제
   - 부모 클래스와 자식 클래스 사이의 개념적인 결합으로 인해 부모 클래스를 변경할 때 자식 클래스도 함께 변경해야 하는 문제

합성을 사용하면 상속이 초래하는 위 세가지 문제를 해결할 수 있다.

### 🔖 11.1.1 불필요한 인터페이스 상속 문제: java.util.properties 와 java.util.stack

```java
public class Properties {
    // properties 에서 상속 관계를 제거하고 HashTable을 인스턴스 변수로 포함시키면 합성 관계로 변경 가능
    private Hashtable<String, String> properties = new Hashtable<>();

    public String setProperty(final String key, final String value) {
        return properties.put(key, value);
    }

    public String getProperty(final String key) {
        return properties.get(key);
    }
}
```
불필요한 hashTable 의 오퍼레이션들이 Properties 클래스의 퍼블릭 인터페이스를 오염시키지 않고 클라이언트는 오직 Properties 에서
정의한 오퍼레이션만 사용할 수 있다.

내부 구현에 밀접하게 결합되는 상속과 달리 합성으로 변경한 Properties는 HashTable의 내부 구현에 관해 알지 못한다.

```java
public class Stack<E> {
    private Vector<E> elements = new Vector<>();

    public E push(E item) {
        elements.addElement(item);
        return item;
    }

    public E pop() {
        if (elements.isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.remove(elements.size() - 1);
    }
}
```
stack 클래스도 마찬가지로 Vector 를 인스턴스 변수로 포함시켜 합성을 할수 있다.

### 🔖 11.1.2 메서드 오버라이딩의 오작용 문제: InstrumentedHashSet

```java
public class InstrumentedHashSet<E> {
    @Getter
    private int addCount = 0;
    private Set<E> set;

    public InstrumentedHashSet(Set<E> set) {
        this.set = set;
    }

    public boolean add(E e) {
        addCount++;
        return set.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return set.addAll(c);
    }
}
```
Stack 을 변경하는 것과 다를것 없다. 하지만 해당 클래스는 HashSet이 제공하는 퍼블릭 인터페이스를 그대로 제공해야 한다.

해결 방법은 set 인터페이스를 사용하여 해결 할수 있다.

```java
public class InstrumentedHashSet<E> implements Set<E> {
    @Getter
    private int addCount = 0;
    private Set<E> set;

    public InstrumentedHashSet(Set<E> set) {
        this.set = set;
    }

    public boolean add(E e) {
        addCount++;
        return set.add(e);
    }

    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return set.addAll(c);
    }

    @Override public int size() {return set.size();}
    @Override public boolean isEmpty() {return false;}
    @Override public boolean contains(Object o) {return false;}
    @Override public Iterator<E> iterator() {return null;}
    @Override public Object[] toArray() {return new Object[0];}
    @Override public <T> T[] toArray(T[] a) {return null;}
    @Override public boolean remove(Object o) {return false;}
    @Override public boolean containsAll(Collection<?> c) {return false;}
    @Override public boolean retainAll(Collection<?> c) {return false;}
    @Override public boolean removeAll(Collection<?> c) {return false;}
    @Override public void clear() {}
}
```
포워딩 - 오버라이딩한 인스턴스 메서드 내에서 내부 HashSet 인스턴스에게 동일한 메서드 호출을 그대로 전달
- 기존 클래스의 인터페이스를 그대로 외부에 제공 하면서 구현에 결합 없이 일부 작동 방식을 변경하고 싶은 경우에 사용할 수 있는 유용한 기법

포워딩 메서드
- 동일한 메서드를 호출하기 위해 추가된 메서드

### 🔖 11.1.3 부모 클래스와 자식 클래스의 동시 수정 문제:PersonalPlayList

```java
public class PersonalPlaylist {
    private Playlist playlist = new Playlist();

    public void append(Song song){
        playlist.append(song);
    }

    public void remove(Song song){
        playlist.getTracks().remove(song);
        playlist.getSingers().remove(song.getSinger());
    }
}
```
Playlist 경우에는 합성으로 변경하더라도 가수별 노래 목록을 유지하기 위해 두클래스가 함께 수정돼야 한다.

그래도 합성이 좋은 이유는 향후 Playlist 내부 구현을 변경하더라도 파급효과를 최대한 PersonalPlaylist 내부로 캡슐화 할수 있기 때문이다.
대부분의 경우 구현에 대한 결합보다는 인터페이스에 대한 결합이 더 좋다는 사실

몽키패치 - 현재 실행 중인 환경에만 영향을 미치도록 지역적으로 코드를 수정하거나 확장하는것을 가리킨다.

## 📖 11.2 상속으로 인한 조합의 폭발적인 증가

상속으로 인해 결합도가 높아지면 코드를 수정하는 데 필요한 작업의 양이 과도하게 늘어나는 경향이 있다. 

일반적으로 두가지 문제점
1. 하나의 기능을 추가하거나 수정하기 위해 불필요하게 많은 수의 클래스를 추가하거나 수정해야 한다.
2. 단일 상속만 지원하는 언어에서는 상속으로 인해 오히려 중복 코드의 양이 늘어날수 있다.

### 🔖 11.2.1 기본 정책 과 부가 정책 조합하기

부가 정책은 통화량과 무관하게 기본 정책에 선택적으로 추가할 수 있는 요금 방식을 의미한다.

부가 정책은 다음과 같은 특징을 가진다.

1. 기본 정책의 계산 결과에 적용된다.
   - 세금 정책은 기본 정책인 RegularPhone 이나 NightlyDiscountPhone의 계산이 끝난 결과에 세금을 부과한다.
2. 선택적으로 적용할 수 있다.
   - 기본 정책의 계산 결과에 세금 정책을 적용할 수도 있고 적용하지 않을 수도 있다.
3. 조합 가능하다.
   - 기본정책에 세금 정책만 적용하는 것도 가능하고, 기본 요금 할인 정책만 적용하는 것도 가능하다. 또한 세금 정책과 기본 요금
   할일 정책을 함께 적용하는 것도 가능해야 한다.
4. 부가 정책은 임의의 순서로 적용 가능하다.
   - 기본 정책에 세금 정책과 기본 요금 할인 정책을 함께 적용할 경우 세금 정책을 적용한 후에 기본 요금 할인 정책을 적용할 수도 있고
   , 기본 요금 할인 정책을 적용한 후에 세금 정책을 적용할 수도 있다.

### 🔖 11.2.2 상속을 이용해서 기본 정책 구현하기

```java
public abstract class Phone {
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

@Getter
@AllArgsConstructor
public class RegularPhone extends Phone {
    private Money amount; // 단위 요금저장
    private Duration seconds; //단위 시간저장
    private List<Call> calls = new ArrayList<>();

    public void call(Call call) {
        this.calls.add(call);
    }

    @Override
    protected Money calculateCallFee(Call call){
        return amount.times(call.getDuration().getSeconds()/seconds.getSeconds());
    }
}

public class NightlyDiscountPhone extends Phone {
    private static final int LATE_NIGHT_HOUR = 22;
    private Duration seconds; //단위 시간저장

    private Money nightlyAmount;
    private Money regularAmount;

    @Override
    protected Money calculateCallFee(Call call){
        if(call.getFrom().getHour() > LATE_NIGHT_HOUR){
            return nightlyAmount.times(call.getDuration().getSeconds()/ seconds.getSeconds());
        }
        return regularAmount.times(call.getDuration().getSeconds()/ seconds.getSeconds());
    }
}
```
RegularPhone 과 NightlyDiscountPhone 의 인스턴스만 단독으로 생성한다는 것은 부가 정책은 적용하지 않고 기본 정책만으로 요금을 계산한다는 것을 의미한다.

### 🔖 11.2.3 기본 정책에 세금 정책 조합하기

```java
public class TaxableRegularPhone extends RegularPhone {
    private final double taxRate;

    public TaxableRegularPhone(Money amount, Duration seconds, List<Call> calls, double taxRate) {
        super(amount, seconds, calls);
        this.taxRate = taxRate;
    }

    @Override
    public Money calculateFee() {
        Money fee = super.calculateFee();
        return fee.plus(fee.times(taxRate));
    }
}
```
가장 간단한 방법 RegulatPhone 을 상속 받아서 super 메소드를 호출한후 계산하는 방법

이렇게 하면 부모 클래스와 결합이 높아지게 된다.

결합도를 낮추는 방법은 자식 클래스가 부모 클래스의 메서드를 호출하지 않도록 부모 클래스에 추상 메서드를 제공하는 것이다.

자식 클래스가 추상 메서드를 오버라이딩 해서 원하는 로직을 제공되도록 수정하면 부모 클래스와 자식 클래스 사이의 결합도를 느슨하게 만들수 있다.

```java
public abstract class Phone {
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
    // 새롭게 추가
    abstract protected Money afterCalculated(Money fee);
}
```

```java
@Getter
@AllArgsConstructor
public class RegularPhone extends Phone {
    private Money amount; // 단위 요금저장
    private Duration seconds; //단위 시간저장
    private List<Call> calls = new ArrayList<>();

    public void call(Call call) {
        this.calls.add(call);
    }

    @Override
    protected Money calculateCallFee(Call call){
        return amount.times(call.getDuration().getSeconds()/seconds.getSeconds());
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee;
    }
}
public class NightlyDiscountPhone extends Phone {
    private static final int LATE_NIGHT_HOUR = 22;
    private Duration seconds; //단위 시간저장

    private Money nightlyAmount;
    private Money regularAmount;

    @Override
    protected Money calculateCallFee(Call call){
        if(call.getFrom().getHour() > LATE_NIGHT_HOUR){
            return nightlyAmount.times(call.getDuration().getSeconds()/ seconds.getSeconds());
        }
        return regularAmount.times(call.getDuration().getSeconds()/ seconds.getSeconds());
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee;
    }
}
```
위 코드에서 알수 있는 것 처럼 부모 클래스에게 추상 메서드를 추가하면 모든 자식 클래스들이 추상 메서드를 오버라이딩 하는 문제가 발생한다.

모든 추상 메서드의 구현이 동일하다는 사실에 주목.
유연성은 유지하면서도 중복 코드를 제거할 수 있는 방법은 Phone에서 afterCalculated 메서드에 대한 기본 구현을 함께 제공하는 것이다.

```java
public abstract class Phone {
    // 공통 부분을 이동
    private List<Call> calls = new ArrayList<>();


    // 공통 부분을 이동
    public Money calculateFee() {
        Money result = Money.ZERO;

        for (Call call : calls) {
            result = result.plus(calculateCallFee(call));
        }
        return afterCalculated(result);
    }

    abstract protected Money calculateCallFee(Call call);

    protected Money afterCalculated(Money fee){
        return fee;
    }
}
```
훅 메서드
- 추상 메서드와 동일하게 자식 클래스에서 오버라이딩할 의도로 메서드를 추가했지만 편의를 위해 기본 구현을 제공하는 메서드를 훅 메서드라고 
부른다.

```java

public class TaxableRegularPhone extends RegularPhone {
    private final double taxRate;

    public TaxableRegularPhone(Money amount, Duration seconds, List<Call> calls, double taxRate) {
        super(amount, seconds, calls);
        this.taxRate = taxRate;
    }

    @Override
    public Money afterCalculated(Money fee) {
        return fee.plus(fee.times(taxRate));
    }
}

public class TaxableNightlyDiscountPhone extends NightlyDiscountPhone {
    private final double taxRate;

    public TaxableNightlyDiscountPhone(Duration seconds, Money nightlyAmount, Money regularAmount, double taxRate) {
        super(seconds, nightlyAmount, regularAmount);
        this.taxRate = taxRate;
    }
    
    @Override
    protected Money afterCalculated(Money fee){
        return fee.plus(fee.times(taxRate));
    }
}
```
세금을 정책을 조합하는 요금과 일반요금을 선택해서 사용하면 된다.

문제는 TaxableNighltDiscountPhone 과 TaxableReularPhone 사이에 코드를 중복했다는 것이다.

자바는 단일 상속만을 지원하기 때문에 상속으로 인해 발생하는 중복 코드 문제를 해결하기가 쉽지 않다.

### 🔖 11.2.4 기본 정책에 기본 요금 할인 정책 조합하기

두 번째 부가 정책인 기본 요금 할인 정책을 Phone 상속 계층에 추가해보자

```java

public class RateDiscountableRegularPhone extends RegularPhone {

    private Money discountAmount;

    public RateDiscountableRegularPhone(Money amount, Duration seconds, List<Call> calls, Money discountAmount) {
        super(amount, seconds, calls);
        this.discountAmount = discountAmount;
    }

    @Override
    public Money afterCalculated(Money fee) {
        return fee.minus(discountAmount);
    }
}


public class RateDiscountableNightlyDiscountPhone extends NightlyDiscountPhone{
    private Money discountAmount;
    
    public RateDiscountableNightlyDiscountPhone(Duration seconds, Money nightlyAmount, Money regularAmount,Money discountAmount) {
        super(seconds, nightlyAmount, regularAmount);
        this.discountAmount = discountAmount;
    }

    @Override
    public Money afterCalculated(Money fee) {
        return fee.minus(discountAmount);
    }
}
```
RateDiscountableRegularPhone 클래스와 RateDiscountableNightlyDiscountPhone 클래스 사이에 중복 코드를 추가했다.

### 🔖 11.2.5 중복 코드의 덫에 걸리다

세금 정책을 적용한 후에 기본 요금 할인 정책을 적용하거나 기본 요금 할인 정책을 적용한 후에 세금 정책을 적용하는 것도 가능해야 한다.

```java
public class TaxableAndRateDiscountableRegularPhone extends TaxableRegularPhone {
    private Money discountAmount;

    public TaxableAndRateDiscountableRegularPhone(Money amount, Duration seconds, List<Call> calls, double taxRate, Money discountAmount) {
        super(amount, seconds, calls, taxRate);
        this.discountAmount = discountAmount;
    }

    @Override
    public Money afterCalculated(Money fee) {
        return super.afterCalculated(fee).minus(discountAmount);
    }

}
```
복잡성 보다 더 큰 문제가 있다. 새로운 정책을 추가하기가 어렵다는 것이다. 
현재의 설계에 새로운 정책을 추가하기 위해서는 불필요하게 많은 수의 클래스를 상속 계층 안에 추가해야 한다.

![image](https://github.com/bithumb-study/study-object/assets/58027908/fca761bd-8433-4a44-8827-2d0e694eec07)
FixedRate 를 추가 하기 위해서는 새로운 5개의 클래스를 추가해야 한다.

클래스 폭발 , 조합의 폭발 문제
- 이처럼 상속의 남용으로 하나의 기능을 추가하기 위해 필요 이상으로 많은 수의 클래스를 추가해야 하는 경우

이문제를 해결할 수 있는 방법은 상속을 포기 하는 것이다.

## 📖 11.3 합성 관계로 변경하기

상속 관계는 컴파일타임에 결정되고 고정되기 때문에 코드를 실행하는 도중에는 변경할 수 없다.
따라서 여러 기능을 조합해야 하는 설계에 상속을 이용하면 모든 조합 가능한 경부렬로 클래스를 추가 해야 한다.

이것이 바로 클래스 폭발 문제.

합성은
- 컴파일 타임 관계를 런타임 관계로 변경함으로써 이 문제를 해결한다.
- 구현이 아닌 퍼블릭 인터페이스에 대해서난 의존할 수 있기 때문에 런타임에 객체의 관계를 변경할 수 있다.
- 구현 시점에 정책들의 관계를 고정시킬 필요가 없으며 실행 시점에 정책들의 관계를 유연하게 변경할 수 있게 된다.
- 런타임 의존성을 구성할 수 있다는 것이 합성이 제공하는 가장 커다란 장점


### 🔖 11.3.1 기본 정책 합성하기

각 정책을 별도의 클래스로 구현

기본 정책과 부가 정책을 포괄하는 RatePolicy 인터페이스 추가
```java
public interface RatePolicy {
    Money calculateFee(Phone phone);
}
```

기본 정책 구현
```java
public abstract class BasicRatePolicy implements RatePolicy {

    @Override
    public Money calculateFee(Phone phone) {
        Money result = Money.ZERO;
        for (Call call : phone.getCalls()) {
            result.plus(calculateCallFee(call));
        }
        return result;
    }

    protected abstract Money calculateCallFee(Call call);
}
```
일반 요금제
```java
@AllArgsConstructor
public class RegularPolicy extends BasicRatePolicy {
    private Money amount;
    private Duration seconds;

    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
}
```
심야 할인 요금제
```java
@AllArgsConstructor
public class NightlyDiscountPolicy extends BasicRatePolicy {
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
```
기본 정책을 이용해 요금 계산 할수 있도록 Phone 수정 
```java
public class PhoneCalculator {
    private RatePolicy ratePolicy;
    private List<Call> calls = new ArrayList<>();

    public PhoneCalculator(RatePolicy ratePolicy) {
        this.ratePolicy = ratePolicy;
    }

    public List<Call> getCalls() {
        return Collections.unmodifiableList(calls);
    }

    public Money calculateFee() {
        return ratePolicy.calculateFee(this);
    }
}
```
PhoneCalculator의 경우처럼 다양한 종류의 객체와 협력하기 위해 합성 관계를 사용하는 경우에는 합성하는 객체의 타입을 인터페이스나 
추상 클래스로 선언하고 의존성 주입을 사용해 런타임에 필요한 객체를 설정할 수 있도록 구현하는 것이 일반적이다.

```java

public class Week11Application {
    public static void main(String... args) {
        //일반 요금
        PhoneCalculator phoneCalculator = new PhoneCalculator(new RegularPolicy(Money.wons(10), Duration.ofSeconds(10)));
        //심야 요금
        PhoneCalculator NightphoneCalculator = new PhoneCalculator(new NightlyDiscountPolicy(Money.wons(5), Money.wons(10), Duration.ofSeconds(10)));
    }
}
```
### 🔖 11.3.2 부가 정책 적용하기

부가 정책은 RatePolicy 인터페이스를 구현해야 하며, 내부에 또 다른 RatePolicy 인스턴스를 합성할수 있어야 한다.

부가 정책 추상클래스 생성
```java
public abstract class AdditionalRatePolicy implements RatePolicy {
    private RatePolicy next;

    public AdditionalRatePolicy(RatePolicy next) {
        this.next = next;
    }

    @Override
    public Money calculateFee(PhoneCalculator phone) {
        Money fee = next.calculateFee(phone);
        return afterCalculated(fee);
    }

    abstract protected Money afterCalculated(Money fee);
}
```

Phone 의 입장에서 AddtionalRatePolicy 는 RatePolicy의 역할을 수행하기 때문에 RatePolicy 구현
또 다른 정책과 조합될수 있도록 RatePolicy 타입의 next라는 이름을 가진 인스턴스 변수를 내부에 포함한다.

세금 정책
```java
public class TaxablePolicy extends AdditionalRatePolicy {

    private double taxRatio;

    public TaxablePolicy(double taxRatio, RatePolicy next) {
        super(next);
        this.taxRatio = taxRatio;
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee.plus(fee.times(taxRatio));
    }
}
```
기본 요금 할인 정책
```java
public class RateDiscountablePolicy extends AdditionalRatePolicy {
    private Money discountAmount;

    public RateDiscountablePolicy(RatePolicy next, Money discountAmount) {
        super(next);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money afterCalculated(Money fee) {
        return fee.minus(discountAmount);
    }
}
```
![image](https://github.com/bithumb-study/study-object/assets/58027908/e0a6ad96-f38a-45df-afe1-a78c79db0ae5)

### 🔖 11.3.3 기본 정책과 부가 정책 합성하기

```java
public class Week11Application {
    public static void main(String... args) {
        //일반 요금
        PhoneCalculator phoneCalculator = new PhoneCalculator(new RegularPolicy(Money.wons(10), Duration.ofSeconds(10)));
        //심야 요금
        PhoneCalculator NightPhoneCalculator = new PhoneCalculator(new NightlyDiscountPolicy(Money.wons(5), Money.wons(10), Duration.ofSeconds(10)));

        PhoneCalculator TaxablePhoneCalculator = new PhoneCalculator(new TaxablePolicy(0.05, new RegularPolicy(Money.wons(10), Duration.ofSeconds(10))));

        PhoneCalculator TaxableAndDiscoutablePhoneCalculator = new PhoneCalculator(new TaxablePolicy(0.05, new RateDiscountablePolicy(Money.wons(1000), new RegularPolicy(Money.wons(10), Duration.ofSeconds(10)))));

    }
}
```
객체를 조합하고 사용하는 방식이 상속을 사용한 방식보다 더 예측 가능하고 일관성이 있다는 사실을 알게될것이다.

합성의 진가는 새로운 클래스를 추가하거나 수정하는 시점이 돼서야 비로소 알 수 있다.

### 🔖 11.3.4 새로운 정책 추가하기
![image](https://github.com/bithumb-study/study-object/assets/58027908/1ab390ae-b2e7-4bf9-ab4a-a55046356542)

위의 그림처럼 오직 하나의 클래스만 추가하고 런타임에 필요한 정책들을 조합해서 원하는 기능을 얻을 수 있다.

더 중요한것은 요구사항을 변경할 때 오직 하나의 클래스만 수정해도 된다는 것이다.

### 🔖 11.3.5 객체 합성이 클래스 상속보다 더 좋은 방법이다.

상속은 부모 클래스의 세부적인 구현에 자식 클래스를 강하게 결합시키기 때문에 코드의 진화를 방해한다.

코드를 재사용 하면서도 건전한 결합도를 유지할 수 있는 더 좋은 방법은 합성을 이용하는 것이다.
상속이 구현을 재사용하는 데 비해 합성은 객체의 인터페이스를 재사용한다.

## 📖 11.4 믹스인

- 객체를 생성할 때 코드 일부를 클래스 안에 석어 넣어 재사용하는 기법
- 합성이 실행 시점에 객체를 조합하는 재사용 방법이라면 믹스인은 컴파일 시점에 필요한 코드 조각을 조합하는 재사용 방법이다.
- 코드 재사용에 특화된 방법이면서도 상속과 같은 결합도 문제를 초래 하지 않는다.
- 합성처럼 유연하면서도 상속처럼 쉽게 코드를 재사용할 수 있는 방법이다.

### 🔖 11.4.1 기본정책 구현하기

일반 요금제
```java
@AllArgsConstructor
public class RegularPolicy extends BasicRatePolicy {
    private Money amount;
    private Duration seconds;

    @Override
    protected Money calculateCallFee(Call call) {
        return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
}
```
심야 할인 요금제
```java
@AllArgsConstructor
public class NightlyDiscountPolicy extends BasicRatePolicy {
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
```
### 🔖 11.4.2 트레이트로 부가 정책 구현하기

코드에서의 Extends 문은 단지 TaxablePolicy 가 사용될수 있는 문맥을 제한할 뿐이다.

상속은 정적이지만 믹스인은 동적이다. 상속은 부모 클래스와 자식 클래스의 관계를 코드를 작성하는 시점에 고정시켜 버리지만 
믹스인은 제약을 둘뿐 실제로 어떤 코드에 믹스인될 것인지를 결정하지 않는다.

super 의 참조가 가리키는 대상이 컴파일 시점이 아닌 실행시점에 결정 된다는 것을 의미한다.

믹스인은 상속보다는 합성과 유사하다. 합성은 독립적으로 작성된 객체들을 실행 시점에 조합해서 더 큰 기능을 만들어내는데 비해
믹스인은 독립적으로 작성된 트레이트 클래스를 코드로 작성 시점에 조합해서 더 큰 기능을 만들어낼수있다.

### 🔖 11.4.3 부가 정책 트레이트 믹스인 하기

---- 

### 🔖 11.4.4 쌓을수 있는 변경

전통적으로 믹스인은 특정한 클래스의 메서드를 재사용하고 기능을 확장하기 위해 사용돼 왔다.

믹스인은 상속 계층 안에서 확장한 클래스보다 더 하위에 위치하게 된다. 다시 말해서 믹스인은 대상 클래스의 자식 클래스처럼
사용될 용도로 만들어 지는것이다.

믹스인을 추상 서브 클래스(abstract subclass) 라고 부르기도 한다.

믹스인은 결론적으로 슈퍼클래스로부터 상속될 클래스를 명시하는 메커니즘을 표현한다.

믹스인을 사용하면 특정한 클래스에 대한 변경 또는 확장을 독립적으로 구현한 후 필요한 시점에 차례대로 추가 할수 있다.
쌓을수 있는 변경이라고 부른다.