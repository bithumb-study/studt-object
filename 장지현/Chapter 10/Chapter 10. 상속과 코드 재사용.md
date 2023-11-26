## Chapter 10. 상속과 코드 재사용

### 01. 상속과 중복 코드
#### DRY 원칙
> Don't Repeat Yourself : 동일한 지식을 중복하지 마라!
> 모든 지식은 시스템 내에서 단일하고, 애매하지 않고, 정말로 믿을 만한 표현 양식을 가져야 한다.
> 한 번, 단 한번 원칙 또는 단일 지점 제어 원칙이라고도 부른다.
> 핵심은 코드안에 중복이 존재해서는 안된다.

<중복코드의 단점>
- 변경을 방해한다.
  - 코드를 수정하는 데 필요한 노력을 몇 배로 증가시킨다.
  
#### 중복과 변경
##### 중복코드 살펴보기
~~~ java
public class Phone { // 통화 요금을 계산할 객체
  private Money amount; // 단위 요금을 저장하는 변수
  private Duration seconds; // 단위 시간을 저장하는 변수
  
  // Call : 개별 통화 기간을 저장하는 클래스
  private List<Call> calls = new ArayList<>(); // 전체 동화 목록을 저장하는 변수
  
  public Phone(Money amount, Duration seconds) {
    this.amount = amount;
    this.seconds = seconds;
  }
  
  public void call(Call call){
    calls.add(call)
  }
  
  public List<Call> getCalls(){
    return calls;
  } 
  
  public Money getAmount() {
    return amount;
  }
  
  public Duration getSeconds(){
    return seconds;
  }
  
  public Money calculateFee() {
    Money result = Money.ZERO;
    
    for(Call call : calls){
      result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
    }
    return result;
  }
  
}
~~~

요구사항이 추가되어 '심야 할인 요금제'라는 새로운 요금 방식을 추가해야한다면 가장 쉽고 빠른 방법은 phone 객체를 복사하여 NightlyDiscountPhone이라는 
새로운 객체를 만들어 수정하는 방법이다.

~~~ java
public class NightlyDiscountPhone { 
  private static final int LATE_NIGHT_HOUR = 22;

  private Money nightAmount; 
  private Money regularAmount; 
  private Duration seconds; 
  private List<Call> calls = new ArayList<>(); 
  
  public NightlyDiscountPhone(Money nightAmount, Money regularAmount, Duration seconds) {
    this.nightAmount = nightAmount;
    this.regularAmount = regularAmount;
    this.seconds = seconds;
  }
  
  public void call(Call call){
    calls.add(call)
  }
  
  public List<Call> getCalls(){
    return calls;
  } 
  
  public Money getAmount() {
    return amount;
  }
  
  public Duration getSeconds(){
    return seconds;
  }
  
  public Money calculateFee() {
    Money result = Money.ZERO;
    
    for(Call call : calls){
      if(call.getFrom().getHour() >= LATE_NIGHT_HOUR){
        result = result.plus(nightAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
      } else {
        result = result.plud(regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
      }
    }
    return result;
  }
}
~~~

위 코드처럼 중복코드를 통한 요구사항 반영은 구현시간을 절약하는 대신 변경에 취약하다.

##### 중복코드 수정하기
기능 추가 : 통화 요금에 부과할 세금을 꼐산하는 것
- 부과되는 세율은 가입자의 핸드폰 마다 다르다
~~~ java
public class Phone { 
  ...
  private double taxRate;
  
  public Phone(Money amount, Duration seconds, double taxRate) {
    ...
    this.taxRate = taxRate;
  }
  
  ...
  
  public Money calculateFee() {
    Money result = Money.ZERO;
    
    for(Call call : calls){
      result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
    }
    return result.plus(result.times(taxRate));
  }
}
~~~

~~~ java
public class NightlyDiscountPhone { 
  ...
  private double taxRate;
  
  public NightlyDiscountPhone(Money nightAmount, Money regularAmount, Duration seconds, double taxRate) {
    ...
    this.taxRate = taxRate;
  }
  
  ...
  
  public Money calculateFee() {
    Money result = Money.ZERO;
    
    for(Call call : calls){
      if(call.getFrom().getHour() >= LATE_NIGHT_HOUR){
        result = result.plus(nightAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
      } else {
        result = result.plud(regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
      }
    }
    return result.plus(result.times(taxRate));
  }
}
~~~

* 추가 요건을 통해 들어나는 중복의 단점
  * phone 과 NightlyDiscountPhone 둘다 이미 계산하는 method를 보유하고있기 때문에 두군데 모두 수정이 필요하다.
  * 중복코드는 항상 같이 수정되야 함으로 수정 시 누락이 있다면 곧바로 버그로 이어진다.
  * 중복코드는 서로 다르게 수정될 가능성이 존재한다.
  * 중복코드는 새로운 중복코드를 부른다.
  
##### 타입 코드 사용하기
두 클래스 사이의 중복 코드를 제거하는 한 가지 방법은 클래스를 하나로 합치는 것

~~~ java
public class Phone { 
  private static final int LATE_NIGHT_HOUR = 22;
  enum PhoneType {REGULAR, NIGHTLY}
  
  private PhoneType type;

  private Money amount; 
  private Money nightAmount; 
  private Money regularAmount; 
  private Duration seconds; 
  private List<Call> calls = new ArayList<>(); 
  
  public Phone(Money amount, Duration seconds) {
    this(PhoneType.REGULAR, amount, Money.ZERO, Money.Zero, seconds);
  }
  
  public Phone(Money nightAmount, Money regularAmount, Duration seconds){
    this(PhoneType.NIGHTLY, Money.Zero, nightAmount, regularAmount, seconds);
  }
  
  public Phone(PhoneType type, Money amount, Money nightAmount, Money regularAmount, Duration seconds){
    this.type = type;
    this.amount = amount;
    this.nightAmount = nightAmount;
    this.regularAmount = regularAmount;
    this.seconds = seconds;
  }
  
  public Money calculateFee() {
    Money result = Money.ZERO;
    
    for(Call call : calls){
      if(type == PhoneType.REGULAR){
        result = result.plus(amount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
      } else {
        if(call.getFrom().getHour() >= LATE_NIGHT_HOUR){
          result = result.plus(nightAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
        } else {
          result = result.plud(regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds()));
        }
      }
    }
    return result;
  }
}
~~~
위 방법은 타입 코드 사용을 통한 중복 코드 제거 방법을 사용하였다.
위 방법의 단점은 타입 코드를 사용하는 클래스는 낮은 응집도와 높은 결합도라는 문제가 있다.

#### 상속을 이용해서 중복 코드 제거하기
타입코드를 사용하지 않고도 중복 코드를 관리할 수 있는 효과적인 방법은 상속이다.
> 이미 존재하는 클래스와 유사한 클래스가 필요하다면 코드를 복사하지 말고 상속을 이용해 코드를 재사용하라는 것이다.

~~~ java
public class NightDiscountPhone extends Phone {
  private static final int LATE_NIGHT_HOUR = 22;
  
  private Money nightlyAmount;
  
  public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds){
    super(regularAmount, seconds);
    this.nightlyAmount = nightlyAmount;
  }
  
  @Overfide
  public Mondy calcuteFee(){
    Money result = super.calculateFee();
    
    Money nightlyFee = Money.ZERO;
    for(Call call : getCalls()){
      if(call.getFrom().getHour() >= LATE_NIGHT_HOUR){
        nightlyFee = nightlyFee.plus(getAmount().minus(nightlyAmount).times(call.getDuration().getSeconds() / getSeconds().getSeconds()));
      }
    }
    return result.minus(nightlyFee);
  }
  
}
~~~
위의 예시 중 요금 계산부분을 통해 알 수 있는것은 상속을 염두해두지 않고 설계된 클래스를 상속을 이용해 재사용 하는것은 쉽지 않다는 것을 알 수 있다.
추가 요건이 들어온다면 코드 중복을 제거하기 위해 상속을 사용했음에도 세금을 꼐산하는 로직을 추가하지 위해 새로운 중복 코드를 만들어야 할 수도 있다.

* 상속을 위한 경고 1.
  자식 클래스의 메서드 안에서 super참조를 이용해 부모 클래스의 메서드를 직접 호출할 경우 두 클래스는 강하게 결합된다.
  super 호출을 제거할 수 있는 방법을 찾아 결합도를 제거하라.

### 02. 취약한 기반 클래스 문제
상속은 자식 클래스와 부모 클래스의 결합도를 높인다.
강한 결합도로 인해 자식 클래스는 부모 클래스의 불필요한 세부 사항에 엮이게 된다.
이저첨 부모 클래스의 변경에 의해 자식 클래스가 영향을 받는 현상을 '취약한 기반 클래스 문제' 라고 부른다.
이 문제는 상속을 사용한다면 피할 수 없는 객체 지향 프로그래밍의 근본적인 취약성이다.

취약한 기반 클래스의 문제 : 캡슐화를 약화시키고 결합도를 높인다.

<상속이 가지는 문제점>
#### 불필요한 인터체이스 상속 문제
* 부모 클래스에서 상속받은 메서드를 사용할 경우 자식 클래스의 규칙이 위반될 수 있다.
  ex> java.util.Stack, java.util.Properties

* 상속을 위한 경고 2.
  상속받은 부모 클래스의 메서득 자식 클래스의 내부 구조에 대한 규칙을 깨트릴 수 있다.

#### 메서드 오버라이딩의 오작용 문제
* 부모 클래스에서 상속받은 메서드 호출이 의도하지 않게 동작할 수 있다.
  ex> InstrumentedHashSet

* 상속을 위한 경고 3.
  자식 클래스가 부모 클래스의 메서드를 오버라이딩할 경우 부모 클래스가 자신의 메서드를 사용하는 방법에 자식 클래스 결할 될 수 있다.

#### 부모 클래스와 자식 클래스의 동시 수정 문제
* 자식 클래스가 부모 클래스의 메서드를 오버라이딩하거나 불필요한 인터페이스를 상속받지 않았음에도 부모 클래스를 수정할 때 자식 클래스를 함께 수정해야 할 수도 있다.

* 상속을 위한 경고 4.
  클래스를 상속하면 결합도로 인해 자식 클래스와 부모 클래스의 구현을 영원히 변경하지 않고나. 자식 클래스와 부모 클래스를 동시에 반영하거나 둘 중 하나를 선택할 수밖에 없다.

### 03.Phone 다시 살펴보기
#### 추상화에 의존하자
> 자식 클래스가 부모 클래스의 구현이 아닌 추상화에 의존하도록 만드는 것
> 부모 클래스와 자식 클래스 모두 추상화에 의존하도록 수정해야 한다.

[저자가 개인적으로 코드 중복을 제거하기 위해 상속을 도입할 때 따르는 두가지 원칙]
1. 두 메서드가 유사하게 보인다면 차이점을 메서드로 추출하라.
2. 부모 클래스의 코드를 하위로 내리지 말고 자식 클래스의 코드를 상위로 올려라.

#### 차이를 메서드로 추출하라.
> 변하는 것으로부터 변하지 않는 것을 분리하라 또는 변하는 부분을 찾고 이를 캡출화 하라 라는 조언을 메서드 수준에서 적용한것.

Phone 과 NightlyDiscountPhone의 차이점은 calculateFee의 for문 안에 구현된 요금 계산 로직이 서로 상이하다.
이 부분을 동일한 이름을 가진 메서드로 추출 -> calculateCallFee()

이를 통해 두 객체의 calculateFee 메서드가 완전 동일해 졌다.

#### 중복 코드를 부모 클래스로 올려라
부모 클래스를 추가하고 phone과 NightlyDiscountPhone이 해당 클래스를 상속 받도록 수정한다.
그 후 두 객체의 공통 부분을 부모 클래스로 이동시킨다.
메서드의 구현은 그대로 두고 공통 부분인 시크니처만 부모 클래스로 이동시킨다.

~~~ java
public abstract class AbstractPhone {
  private List<Call> calls = new ArrayList<>();
  
  public Money calculateFee() {
    Money result = Money.ZERO;
    for(Call call : calls){
      result = result.plus(calculateCallFee(call));
    }
    return result;
  }  
  
  abstract protected Money calculateCallFee(Call call);
}

public class Phone extends AbstractPhone {
  private Money amount;
  private Duration seconds;
  
  public Phone(Money amount, Duration seconds){
    this.amount = amount;
    this.seconds = seconds;
  }
  
  @Override
  protected Money calculateCallFee(Call call){
    return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
  }
}

public class NightlyDiscoundPhone extends AbstractPhone {
  private static final int LATE_NIGHT_HOUR = 22;
  
  private Money nigtlyAmount;
  private Money regularAmount;
  private Duration seconds;
  
  public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration second){
    this.nightlyAmount = nightlyAmount;
    this.regularAmount = regularAmount;
    this.second = second;
  }
  
  @Override
  protected Money calculateCallFee(Call call){
    if(call.getFrom().getHour() >= LATE_NIGHT_HOUR){
      return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
    }
    return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
  }
  
}
~~~

#### 추상화가 핵심이다.
위 예시가 추상화를 통해 얻은 장점
1. 세 클래스는 각각 하나의 변경 이유만을 가진다.
   단일 책임 원칙을 준수하기 떄문에 응집도가 높다.
2. 자식클래스가 부모 클래스의 구체적인 구현에 의존되지 않는다.
3. 새로운 요구사항을 추가하는데도 수월하다.

#### 의도를 드러내는 이름 선택하기

#### 세금 추가하기

### 04. 차이에 의한 프로그래밍
> 기존 코드와 다른 부분만을 추가함으로써 애플리케이션의 기능을 확장하는 방법
> 목표 : 중복코드를 제거하고 코드를 재사용하는 것
