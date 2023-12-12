# 📚 12장 다형성

## 📖 12.1 다형성

컴퓨터 과학에서는 다형성을 하나의 추상 인터페이스에 대해 코드를 작성하고 이 추상 인터페이스에 대해 서로 다른 구현을 연결할 수 있는
능력으로 정의한다.

간단하게 말해서 다형성은 여러 타입을 대상으로 동작할 수 있는 코드를 작성할 수 있는 방법

![image](https://github.com/bithumb-study/study-object/assets/58027908/24886b6d-cbe9-4324-8d19-3260230c6274)

오버로딩 다형성

- 일반적으로 하나의 클래스 안에 동일한 이름의 메서드가 존재하는 경우

```java
public class Money {
    public Money plus(Money amount) {...}

    public Money plus(BigDecimal amount) {...}

    public Money plus(long amount) {...}
}
```

강제 다형성

- 언어가 지원하는 자동적인 타입 변환이나 사용자가 직접 구현한 타입 변환을 이용해 동일한 연산자를 다양한 타입에 사용할 수 있는 방식을 가리킨다.

매개변수 다형성

- 제네릭 프로그래밍과 관련이 높은테 클래스의 인스턴스 변수나 메서드의 매개변수 타입을 임의의 타입으로 선언한 후 사용하는 시점에 구체적인 타입으로 지정하는 방식

포함 다형성

- 메시지가 동일하더라도 수신한 객체의 타입에 따라 실제로 수행되는 행동이 달라지는 능력
- 서브타입 다형성이라고 불리고 가장 일반적인 다형성

## 📖 12.2 상속의 양면성

객체지향 프로그램을 작성하기 위해서는 항상 데이터와 행동이라는 두 가지 관점을 함께 고려해야 한다.

상속 역시 예외가 아니다.

데이터 관점 상속

- 상속을 이용하면 부모 클래스에서 정의한 모든 데이터를 자식 클래스의 인스턴스에 자동으로 포함시킬수 있다.

행동 관점의 상속

- 데이터뿐만 아니라 부모 클래스에서 정의한 일부 메서드 역시 자동으로 자식 클래스에 포함시킬수 있다.

상속의 목적은 코드 재사용이 아니라 프로그램을 구성하는 개념들을 기반으로 다형성을 가능하게 하는 타입 계층을 구축하기 위한것이다.

상속 메커니즘 이해하기 위한 개념

- 업캐스팅
- 동적 메서드 탐색
- 동적 바인딩
- self 참조
- super 참조

### 🔖 12.2.1 상속을 사용한 강의 평가

#### Lecture 클래스 살펴보기

```java

@AllArgsConstructor
public class Lecture {
    private int pass;
    private String title;
    private List<Integer> scores = new ArrayList<>();

    public double average() {
        return scores.stream()
                .mapToInt(Integer::intValue)
                .average().orElse(0);
    }

    public List<Integer> getScores() {
        return Collections.unmodifiableList(scores);
    }

    public String evaluate() {
        return String.format("Pass:%d Fail:%d", passCount(), failCount());
    }

    private long passCount() {
        return scores.stream().filter(score -> score >= pass).count();
    }

    private long failCount() {
        return scores.size() - passCount();
    }
}

public class Week12Application {

    public static void main(String... args) {
        Lecture lecture = new Lecture("객체지향 프로그래밍", 70, List.of(81, 95, 75, 50, 45));

        String evaluration = lecture.evaluate();
    }
}
```

#### 상속을 이용해 Lecture 클래스 재사용하기

Lecture 클래스는 새로운 기능을 구현하는데 필요한 대부분의 데이터와 메서드를 포함하고 있다.
따라서 상속을 이용하면 쉽고 빠르게 새로운 기능을 추가할수 있다.

```java
public class GradeLecture extends Lecture {
    private final List<Grade> grades;

    public GradeLecture(String title, int pass, List<Integer> scores, List<Grade> grades) {
        super(title, pass, scores);
        this.grades = grades;
    }

    @Override
    public String evaluate() {
        return super.evaluate() + ", " + gradeStatistics();
    }

    private String gradeStatistics() {
        return grades.stream()
                .map(this::format)
                .collect(joining(" "));
    }

    private String format(Grade grade) {
        return String.format("%s:%d", grade.getName(), gradeCount(grade));
    }

    private long gradeCount(Grade grade) {
        return getScores().stream()
                .filter(grade::include)
                .count();
    }
}

@AllArgsConstructor
public class Grade {
    @Getter
    private String name;
    private int upper;
    private int lower;

    public boolean isName(String name) {
        return this.name.equals(name);
    }

    public boolean include(int score) {
        return score >= lower && score <= upper;
    }
}
```

evaluate 메서드의 시그니처가 완전 동일하다.

부모 클래스와 자식 클래스에 동일한 시그니처를 가진 메서드가 존재할 경우 자식 클래스의 메서드 우선순위가 더 높다.
우선순위가 더 높다는 것은 메시지를 수신했을 때 부모 클래스의 메서드가 아닌 자식 클래스의 메서드가 실행 된다는것을 의미한다.

메서드 오버라이딩

- 자식 클래스안에 상속받은 메서드와 동일한 시그니처의 메서드를 재정의해서 부모 클래스의 구현을 새로운 구현으로 대체하는 것

```java
public class GradeLecture extends Lecture {
    private final List<Grade> grades;

    public GradeLecture(String title, int pass, List<Integer> scores, List<Grade> grades) {
        super(title, pass, scores);
        this.grades = grades;
    }

    public double average(String gradeName) {
        return grades.stream()
                .filter(each -> each.isName(gradeName))
                .findFirst()
                .map(this::gradeAverage)
                .orElse(0d);
    }

    private double gradeAverage(Grade grade) {
        return getScores().stream()
                .filter(grade::include)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }
}
```

자식 클래스에 부모 클래스에는 없던 새로운 메서드를 추가하는 것도 가능하다.

메서드 오버로딩

- 부모 클래스에서 정의한 메서드와 이름은 동일하지만 시그니처는 다른 메서드를 자식 클래스에 추가하는 것

### 🔖 12.2.2 데이터 관점의 상속

```java
public class Week12Application {

    public static void main(String... args) {
        Lecture lecture = new Lecture("객체지향 프로그래밍", 70, List.of(81, 95, 75, 50, 45));

        Lecture gradeLecture = new GradeLecture("객체지향 프로그래밍", 70, List.of(81, 95, 75, 50, 45)
                , List.of(
                new Grade("A", 100, 95),
                new Grade("B", 94, 80),
                new Grade("C", 79, 70),
                new Grade("D", 69, 50)
        ));

        String evaluration = lecture.evaluate();
    }
}

```

GradeLecture 클래스의 인스턴스는 직접 정의한 인스턴스 변수 뿐만 아니라 부모 클래스인 Lecture 가 정의한 인스턴스 변수도 함께 포함한다.

인스턴스를 참조하는 lecture 는 GradeLecture의 인스턴스를 가리키기 때문에 특별한 방법을 사용하지 않으면 GradeLecture 안에 포함된 Lecture 의
인스턴스에 직접 접근할 수 없다.
![image](https://github.com/bithumb-study/study-object/assets/58027908/301eae98-eb48-4189-93e0-06a74606a66b)

데이터 관점에서 상속은 자식클래스의 인스턴스 안에 부모 클래스의 인스턴스를 포함하는 것으로 볼 수 있다.
자식 클래스의 인스턴스는 자동으로 부모 클래스에서 정의한 모든 인스턴스 변수를 내부에 포함하게 되는 것이다.

### 🔖 12.2.3 행동 관점의 상속

부모 클래스가 정의한 일부 메서드를 자식 클래스의 메서드로 포함시키는 것을 의미한다.
공통적으로 부모 클래스의 모든 퍼블릭 메서드는 자식 클래스의 퍼블릭 인터페이스에 포함된다.

런타임에 시스템이 자식 클래스에 정의되지 않은 메서드가 있을 경우 이 메서드를 부모 클래스 안에서 탐색하기 때문이다.
행동 관점에서 상속과 다형성의 기본적인 개념을 이해하기 위해서는 상속 관계로 연결된 클래스 사이의 메서드 탐색 과정을 이해하는 것이 가장 중요하다.

객체의 경우에는 서로 다른 상태를 저장할 수 있도록 각 인스턴스별로 독립적인 메모리를 할당받아야 한다.
하지만 메서드의 경우에는 동일한 클래스의 인스턴스 끼리 공유가 가능하기 떄문에 클래스는 한번만 메모리에 로드하고 각 인스턴스별로 클래스를
가리키는 포인터를 갖게 하는 것이 경제적이다.

![image](https://github.com/bithumb-study/study-object/assets/58027908/e5fc4b07-f34c-4b53-a6f0-e423afcf33b1)

## 📖 12.3 업캐스팅과 동적 바인딩

### 🔖 12.3.1 같은 메시지, 다른 메서드

```java

@AllArgsConstructor
public class Professor {
    private String name;
    private Lecture lecture;

    public String complieStatistics() {
        return String.format("[%s] %s - Avg: %.1f", name, lecture.evaluate(), lecture.average());
    }
}

public class Week12Application {

    public static void main(String... args) {
        Lecture lecture = new Lecture("객체지향 프로그래밍", 70, List.of(81, 95, 75, 50, 45));

        Lecture gradeLecture = new GradeLecture("객체지향 프로그래밍", 70, List.of(81, 95, 75, 50, 45)
                , List.of(
                new Grade("A", 100, 95),
                new Grade("B", 94, 80),
                new Grade("C", 79, 70),
                new Grade("D", 69, 50)
        ));

        Professor professor = new Professor("다익스트라", lecture);
        Professor gradeProfessor = new Professor("다익스트라", gradeLecture);
    }
}
```

코드 안에서 선언된 참조 타입과 무관하게 실제로 메시지를 수신하는 객체의 타입에 따라 실행되는 메서드가 달라질 수 있는 것은 업캐스팅과 동적 바인딩이라는
메커니즘이 작용하기 때문이다.

업캐스팅

- 부모 클래스(lecture) 타입으로 선언된 변수에 자식 클래스(GradeLecture) 의 인스턴스를 할당하는 것이 가능하다.
- 서로 다른 클래스의 인스턴스를 동일한 타입에 할당하는 것을 가능하게 해준다.
- 부모 클래스에 대해 작성된 코드를 전혀 수정하지 않고도 자식 클래스에 적용할 수 있다.

동적 메서드 탐색 (바인딩)

- 선언된 변수의 타입이 아니라 메시지를 수신하는 객체의 타입에 따라 실행되는 메서드가 결정된다. 이것은 객체지향 시스템이 메시지를 처리할 적절한 메서드를 컴파일 시점이 아니라 실행 시점에 결정하기 떄문에 가능하다.
- 부모 클래스의 타입에 대해 메시지를 전송하더라도 실행 시에는 실제 클래스 기반으로 실행될 메서드가 선택 되게 해준다.
- 코드를 변경하지 않고도 실행 되는 메서드를 변경할수 있다.

개방-폐쇄 원칙과 의존성 역전 원칙

- 업캐스팅과 동적 메서드 탐색은 코드를 변경하지 않고도 기능을 추가할 수 있게 해주며 이것은 개방-폐쇄 원칙의 의도와도 일치한다.
- 원칙은 유연하고 확장 가능한 코드를 만들기 위해 의존관계를 구조화 하는 방법을 설명한다. 업캐스팅과 동적 메서드 탐색은 상속을 이용해 개방-폐쇄 원칙을 따르는 코드를 작성할 때 하부에서 동작하는 기술적인 내부
  메커니즘을 설명
- 개방-폐쇄 원칙이 목적이라면 업캐스팅과 동적 메서드 탐색은 목적에 이르는 방법이다.

### 🔖 12.3.2 업캐스팅

부모 클래스의 인스턴스 대신 자식 클래스의 인스턴스를 사용하더라도 메시지를 처리하는 데는 아무런 문제가 없으며, 컴파일러는 명시적인
타입 변환 없이도 자식 클래스가 부모 클래스를 대체할수 있게 허용한다.

이런 특성을 활용하는 대표적인 두가지 대입문, 메서드의 파라미터 타입

모든 객체지향 언어는 명시적으로 타입을 변환하지 않고도 부모 클래스 타입의 참조 변수에 자식 클래스의 인스턴스를 대입할 수 있게 허용한다.

```java
public class Week12Application {

    public static void main(String... args) {
        Lecture lecture = new Lecture("객체지향 프로그래밍", 70, List.of(81, 95, 75, 50, 45));

        Lecture gradeLecture = new GradeLecture("객체지향 프로그래밍", 70, List.of(81, 95, 75, 50, 45)
                , List.of(
                new Grade("A", 100, 95),
                new Grade("B", 94, 80),
                new Grade("C", 79, 70),
                new Grade("D", 69, 50)
        ));

        GradeLecture gradeLecture1 = (GradeLecture) lecture;

        Professor professor = new Professor("다익스트라", lecture);
        Professor gradeProfessor = new Professor("다익스트라", gradeLecture);
        Professor downCastingProfessor = new Professor("다익스트라", lecture);
    }
}
```

다운 캐스팅

- 부모 클래스의 인스턴스를 자식 클래스 타입으로 변환 하기 위해서는 명시적인 타입 캐스팅이 필요

### 🔖 12.3.3 동적 바인딩

정적바인딩, 초기 바인딩, 컴파일타임 바인딩

- 전통적인 언어에서 함수를 실행하는 방법은 함수를 호출하는것
- 컴파일타임에 호출할 함수를 결정하는 방식

동적 바인딩, 지연 바인딩

- 메서드를 실행하는 방법은 메시지를 전송하는것
- 실행될 메서드를 런타임에 결정하는 방식

## 📖 12.4 동적 메서드 탐색과 다형성

객체지향 시스템은 다음 규칙에 따라 실행할 메서드를 선택

1. 메시지를 수신한 객체는 먼저 자신을 생성한 클래스에 적합한 메서드가 존재하는지 검사한다.
   존재하면 메서드를 실행하고 탐색을 종료한다.
2. 메서드를 찾지 못했다면 부모 클래스에서 메서드 탐색을 계속 한다. 이 과정은 메서드를 찾을 때까지 상속 계층을 따라 올라가며 계속된다.
3. 상속 계층의 가장 최상위 클래스에 이르렀지만 메서드를 발견하지 못한 경우 예외를 발생시며 탐색을 중단한다.

메시지 탐색과 관련한 중요한 변수 -> self 참조

- 객체가 메시지를 수신하면 컴파일러는 self 참조라는 임시 변수를 자동으로 생성한 후 메시지를 수신한 객체를 가리키도록 설정한다.

클래스 정보 안에는 클래스 안에 구현된 전체 메서드의 목록이 포함돼 있는데 이 목록 안에 메시지를 처리할 적절한 메서드가 존재하면 해당 메서드를 실행한 후
동적 메서드 탐색을 종료한다.

![image](https://github.com/bithumb-study/study-object/assets/58027908/93617e21-aec5-43b7-9933-728eefa05747)

메서드 탐색은 자식 클래스에서 부모 클래스의 방향으로 진행된다. 따라서 항상 자식 클래스의 메서드가 부모 클래스의 메서드보다 먼저 탐색 되기 때문에
자식 클래스에 선언된 메서드가 부모 클래스의 메서드보다 더 높은 우선수위를 가지게 된다.

자동적인 메서드위임

- 자식 클래스는 자신이 이해할 수 없는 메시지를 전송받은 경우 상속 계층을 따라 부모 클래스에게 처리를 위임한다.

동적인 문맥

- 메시지를 수신했을 때 실제로 어떤 메서드를 실행할지를 결정하는 것은 컴파일 시점이 아닌 실행 시점에 이뤄지며, 메서드를 탐색하는 경로는 self 참조를 이용해서 결정한다.

### 🔖 12.4.1 자동적인 메서드 위임

동적 메서드 탐색의 입장에서 상속 계층은 메시지를 수신한 객체가 자신이 이해할 수 없는 메시지를 부모 클래스에게 전달하기 위한 물리적인 경로를 정의하는 것으로 볼수 있다.

메시지는 상속 계층을 따라 부모 클래스에게 자동으로 위임된다.
이런 관점에서 상속 계층을 정의하는 것은 메서드 탐색 경로를 정의하는 것과 동일하다.

자식 클래스에서 부모 클래스의 방향으로 자동으로 메시지 처리가 위임 되기 때문에 자식 클래스에서 어떤 메서드를 구현하고 있느냐에 따라 부모 클래스에 구현된
메서드의 운명이 결정되기도 한다.

메서드 오버라이딩
- 자식 클래스의 메서드가 동일한 시그니처를 가진 부모 클래스의 메서드보다 먼저 탐색되기 때문에 벌어지는 현상

메서드 오버로딩
- 동일한 시그니처를 가지는 자식 클래스의 메서드는 부모 클래스의 메서드를 감추지만 이름만 같고 시그니처가 완전히 동일하지 않은 메서드들은 상속 계층에
걸쳐 사이좋게 공존할수 있다.

#### 메서드 오버라이딩

자식 클래스와 부모 클래스 양쪽 모두에 동일한 시그니처를 가진 메서드가 구현돼 있다면 자식 클래스의 메서드가 먼저 검색된다.
따라서 자식 클래스의 메서드가 부모 클래스의 메서드를 감추는 것처럼 보이게 된다.

#### 메서드 오버로딩

메서드 오버라이딩은 자식 클래스가 부모 클래스에 존재하는 메서드와 동일한 시그니처를 가진 메서드를 재정의해서 부모 클래스의 메서드를 감추는 현상을 
가리킨다.

시그니처가 다르기 떄문에 동일한 이름의 메서드가 공존하는 경우를 메서드 오버로딩이라고 부른다.

메서드 오버라이딩은 메서드를 감추지만 메서드 오버로딩은 사이좋게 공존한다.

다시말해서 클래스 관점에서 오버로딩된 모든 메서드를 호출할 수 있는 것이다.

### 🔖 12.4.2 동적인 문맥 

메시지를 수신한 객체가 무엇이냐에 따라 메서드 탐색을 위한 문맥이 동적으로 바뀐다. 그리고 이 동적인 문맥을 결정하는 것은 바로 메시지를 
수신한 객체를 가리키는 self 참조다.

동일한 코드라고 하더라도 self 참조가 가리키는 객체가 무엇인지에 따라 메서드 탐색을 위한 상속 계층의 범위가 동적으로 변한다.
따라서 self 참조가 가리키는 객체의 타입을 변경함으로써 객체가 실행 될 문맥을 동적으로 바꿀수 있다.

![image](https://github.com/bithumb-study/study-object/assets/58027908/9ad6e715-63b9-4d6a-a659-81810fa260ca)
self 전송은 자식 클래스에서 부모 클래스 방향으로 진행되는 동적 메서드 탐색 경로를 다시 self 참조가 가리키는 원랙의 자식 클래스로 이동시킨다.

self 전송이 깊은 상속 계층과 계층 중간중간에 함정처럼 숨겨져 있는 메서드 오버라이딩과 만나면 극단적으로 이해하기 어려운 코드가 만들어진다.

### 🔖 12.4.3 이해할 수 없는 메시지

이해 할수 없는 메시지를 처리하는 방법은 프로그래밍 언어가 정적 타입 언어에 속하는지, 동적 타입 언어에 속하는지에 따라 달라진다.

#### 정적 타입 언어와 이해할 수 없는 메시지

정적 타입 언어에서는 코드를 컴파일할 때 상속 계층 안의 클래스들이 메시지를 이해할 수 있는지 여부를 판단한다.
따라서 상속 계층 전체를 탐색한 후에도 메시지를 처리할 수 있는 메서드를 발견하지 못했다면 컴파일 에러를 발생시킨다.

이해할수 없는 메시지를 처리할 메서드가 존재하는 지 검색하고 더이상 찾을수 없다면 컴파일 에러를 발생시켜 메시지를 처리할수 없다는 사실을 프로그래머에게
알린다.

#### 동적 타입 언어와 이해할 수 없는 메시지

동적 타입 언어 역시 메시지를 수신한 객체의 클래스부터 부모 클래스의 방향으로 메서드를 탐색한다.

차이점은 동적 타입 언어에는 컴파일 단계가 존재하지 않기 때문에 실제로 코드를 실행해보기 전에는 메시지 처리 가능 여부를 판다할 수 없다는 점이다.

동적 타입 언어는 이해할 수 없는 메시지를 처리할 수 있는 능력을 가짐으로써 메시지가 선언된 인터페이스와 메서드가 정의된 구현을 분리 할수 있다.

그러나 동적 타입 언어의 이러한 동적인 특성과 유연성은 코드를 이해하고 수정하기 어렵게 만들뿐만 아니라 디버깅 과정을 복잡하게 만들기도 한다.

정적 타입 언어에는 이런 유연성이 부족하지만 좀더 안정적이다. 모든 메시지는 컴파일타임에 확인되고 이해할 수 없는 메시지는 컴파일 에러로 이어진다.

이해할수 없는메시지와 도메인-특화 언어
- 이해할수 없는 메시지를 처리할 수 있는 동적 타입 언어의 특징은 메타 프로그래밍 영역에서 진가를 발휘한다.
- 특히 동적 타입 언어의 이러한 특징으로 인해 동적 타입 언어는 정적 타입 언어보다 더 쉽고 강력한 도메인-특화 언어를 개발할 수 있는것으로 간주된다.
- 동적 리셉션

### 🔖 12.4.4 self 대 super 

self 참조의 가장 큰 특징은 동적이라는 점이다. 
- self 참조는 메시지를 수신한 객체의 클래스에 따라 메서드 탐색을 위한 문맥을 실행 시점에 결정한다. 

super 참조
- 자식 클래스에서 부모 클래스의 구현을 재사용해야 하는 경우 
- 대부분의 객체지향 언어들은 자식 클래스에서 부모 클래스의 인스턴스 변수나 메서드에 접근하기 위해 사용할 수 있는 super 참조라는 내부 변수를 제공한다.

```java
public class FormattedGradeLecture extends GradeLecture {
    public FormattedGradeLecture(String title, int pass, List<Integer> scores, List<Grade> grades) {
        super(title, pass, scores, grades);
    }

    public String formatAverage() {
        //GradeLecture 에 정의되어 있지않아도 그 위의 부모클래스에서 메서드를 찾는다.
        return String.format("Avg: %1.1f", super.average());
    }
}
```
![image](https://github.com/bithumb-study/study-object/assets/58027908/47334476-0e6c-4508-b10c-e5a54221e087)

super 참조의 정확한 의도는 '지금 이 클래스의 부모 클래스에서부터 메서드 탐색을 시작하세요'다.
만약 부모 클래스에서 원하는 메서드를 찾지 못한다면 더 상위의 부모 클래스로 이동하면서 메서드가 존재하는지 검사한다.

이것은 super 참조를 통해 실행하고자 하는 메서드가 반드시 부모 클래스에 위치하지 않아도 되는 유연성을 제공한다.

super 전송
- super 참조를 통해 메시지를 전송하는 것은 마치 부모 클래스의 인스턴스에게 메시지를 전송하는 것

super 전송과 동적 바인딩
- 상속에서 super 가 컴파일 시점에 미리 결정된다고 설명헀지만 super 를 런타임에 결정하는 경우도 있다. 
- 믹스인을 설명하면서 예로 들었던 스칼라의 트레이트는 super 대상을 믹스인되는 순서에 따라 동적으로 결정한다.
- 따라서 사용하는 언어 특성에 따라서 컴파일 시점이 아닌 실행 시점에 super 대상이 결정될수도 있다.
- 대부분의 객체지향 언어 에서는 상속을 사용하는 경우에는 super 가 컴파일 시점에 결정 

## 📖 12.5 상속 대 위임

### 🔖 12.5.1 위임과 self 참조

![image](https://github.com/bithumb-study/study-object/assets/58027908/14a3044b-30f3-4ddc-adc7-3817fec8bab9)
self 참조는 항상 메시지를 수신한 객체를 가리키기 때문이다. 따라서 메서드 탐색 중에는 자식 클래스의 인스턴스와 부모 클래스의 인스턴스가 동일한 self
참조를 공유하는 것으로 봐도 무방하다.

상속 계층을 구성하는 객체들 사이에서 self 참조를 공유하기 때문에 개념적으로 각 인스턴스에서 self 참조를 공유하는 self 라는 변수를 포함하는 것처럼 표현

위임
- 자신이 수신한 메시지를 다른 객체에게 동일하게 전달해서 처리를 요청 하는 것
- 자신이 정의하지 않거나 처리할 수 없는 속성 또는 메서드의 탐색 과정을 다른 객체로 이동시키기 위해 사용한다.
- 항상 현재의 실행 문맥을 가리키는 self 참조를 인자로 전달 이것이 self 참조를 전달하지 않는 포워딩과의 차이점

### 🔖 12.5.2 프로토 타입 기반의 객체지향 언어

클래스가 존재하지 않고 오직 객체만 존재하는 프로토타입 기반의 객체지향 언어에서 상속을 구현하는 유일한 방법은 객체 사이의 위임을 이용하는것



