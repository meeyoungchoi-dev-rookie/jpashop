# JPA 실전 프로젝트 1

## 환경설정 및 세팅
### 설정정보

start.spring.io에 설정

project : gradle

language : java

spring boot 버전 : 2.6.6

group : jpabook

artifact : jpashoop

dependencies

- web
- lombok
- thymeleaf
- jpa
- h2database

### view 환경 설정

- 타임리프를 사용하려면 `<html xmlns:th="http://www.thymeleaf.org">` 를 넣어줘야 한다
- html 파일에서 수정후 서버를 재시작 해줘야 수정된 내용이 반영된다
- 이를 해결하기 위해 build.gradle에 devtools 라이브러리를 추가했다
- html 파일 수정후 build에서 recompile 만 해주면 html 수정내용이 반영된다
- devtools가 애플리케이션 재시작과 리로드를 자동화 해준다


### H2 데이터베이스 설치

- [https://h2database.com/html/main.html](https://h2database.com/html/main.html) 들어가서 컴퓨터에 맞는 버전으로 다운 받는다
- 압축을 푼다
- H2가 설치된 경로 밑에 bin 디렉토리로 들어간다
- h2.bat을 실행한다
- localhost:8082~~~로 바꿔준다
- 데이터베이스 파일을 생성해야 한다
- jdbc:h2:~/jpashop 으로 한후 연결을 누른다
- 다시 나온다
- 이후 부터는 jdbc:h2:tcp://localhost/~/jpashop 으로 접속한다

## JPA 설정 정보 분석

- `yaml`  - 시스템간 데이터를 주고 받을 때 데이터 필요한 데이터 포맷에 대한 약속
- xml , json 과 같은 데이터 포맷이 있다
- yaml은 다른 포맷보다 코드가독성이 높아 많은 곳에서 사용된다

- 서버가 실행될때 마다 DB를 초기화 시키겠다
- 내장 데이터베이스를 사용할때 기본값이 create-drop이다
- 스키마를 생성한다
- 이전에 스키마에 존재하던 데이터는 손실된다

```yaml
jpa:
    hibernate:
      ddl-auto: create
```

- 콘솔에 JPA 실행 쿼리를 출력

```yaml
properties:
      hibernate:
        show_sql: true
```

- JPA에서 실행되는 쿼리를 가독성 있게 표현해 준다

```yaml
format_ssql: true
```

- 쿼리의 ?에 실제로 어떤 값이 들어갔는지 확인하기 위한 옵션

```yaml
logging:
  level:
    org.hibernate.SQL: debug
```

- insert 쿼리시 ? 안에 들어가는 데이터를 보여준다

```yaml
org.hibernate.type: trace
```

```sql
insert into member (user_name, id) values (?, ?)
insert into member (user_name, id) values ('memberA', 1);
```

### @Id @GeneratedValue

- @GeneratedValue  어노테이션을 사용하여 기본 키 생성을 데이터베이스에 위임한다
- DB가 알아서 PK 컬럼을 AUTO_INCREMENT 해준다
- MYSQL , PostgreSQL , SQL Server DB2 등에서 사용됨
- id 값을 설정하지 않고 insert 쿼리를 날리면 데이터베이스가 id값을 세팅해 준다
- id 값은 DB에 값이 들어간 후에 알수 있다
- **문제점**
- 1차 캐시안에있는 @ID 값을 DB에 넣기 전까지는 세팅할 수 없다
- 예외적으로 entityManager.persist() 가 호추로디는 시점에 DB에 insert 쿼리를 날리고 DB에서 식별자를 조회하여 1차 캐시에 값을 넣어준다
- 따라서 ID를 조회하기위한  select 문을 다시 날리지 않아도 된다

### **@Transactional**

- JPA는 트랜잭션이 commit 될때 객체의 변경을 감지한다
- 메서드가 실행된 후 transaction commit 코드를 삽입하여 객체의 변경을 감지한다
- commit이 되면 영속성 컨텍스트에서 실제 db로 데이터를 반영한다


### 발생했던 에러

```java
No EntityManager with actual transaction available for current thread - cannot reliably process 'persist' call
```

- 리퍼지터리에 @Transactional 어노테이션을 붙이지 않아 메서드가 실행되고 난후 데이터가 롤백되어 버렸다
- 영속성 컨텍스트에 들어있는 엔티티를 디비에 반영해 줘야 하는데 반영해 줄수 없기 때문에 에러가 발생되었다
- 해결책 리포지터리 메서드에 @Transactional 어노테이션을 붙인다
- 테스트시에도 동일하게 적용되므로 붙여줘야 한다
- 테스트 메서드 실행후 롤백 되는 것을 확실하게 방지하기 위해
- @Rollback(false) 어노테이션을 사용한다

```text
Could not resolve all files for configuration ':compileClasspath'.
```

- build.gradle에 주입한 라이브러리가 버전이 맞지 않을때 발생하는 에러
- jpa 쿼리 실행시 ?에 들어가는 값을 쿼리가 실행될때 직접 보여주기 위해 `com.github.gavlyukovskiy:p6spy-spring-boot-starter:` 라이브러리를 사용했다
- 그런데 버전을 잘못설정해 줘서 에러가 발생했다
- 버전을 맞춰줬더니 에러가 해결되었다

### 엔티티 생성시 기본생성자가 필요한 이유

- 테스트 중 기본생성자가 없다는 에러가 발생했었다
- 객체의 일관성을 유지하기 위해 객체 생성 시점에 값을 세팅하여 setter의 사용을 줄일 수 있게 하기 위함
- JPA의 구현체인 hibernate에서 제공하는 다양한 기능을 활욯가ㅣ 위해 기본생성자가 필요하다

### persist 메서드와 트랜잭션

- persist 메서드는 트랜잭션 범위내에서 실행해야 한다
- 실행시점에 영속 컨텍스트에 엔티티를 저장한다
- 트랜잭션을 커밋하는 시점에 insert 쿼리가 실행된다
- 트랜잭션 범위에서 persist 메서드를 실행하지 않으면 실제로 DB에 반영되지 않는다


## JPA 쇼핑몰 요구사항 분석
### 기능 목록

- 회원 기능
    - 회원 등록
    - 회원 조회
- 상품 기능
    - 상품 등록
    - 상품 수정
    - 상품 조회
- 주문 기능
    - 상품 주문
    - 주문 내역 조회
    - 주문 취소
- 기타 요구사항
    - 상품은 재고 관리가 필요하다
    - 상품의 종류는 도서 , 음반 , 영화가 있다
    - 상품을 카테고리로 구분할 수 있다
    - 상품 주문시 배송 정보를 입력할 수 있다


### 도메인 모델과 테이블 설계
![jpa_shop_도메인모델과 테이블 설계](https://user-images.githubusercontent.com/42866800/164470908-231b583d-c036-4c97-87d9-ab734b82d6f6.png)

- 회원은 여러개의 주문을 할 수 있다 (1 : N)
    - 회원이 주문을 하는 것 처럼 보이지만 주문이 회원정보를 갖고 있으면 된다
    - 그래서 1 : N 관계를 만들지 않아도 되지만 1 : N 예시를 위해 만들었다
- 여러개의 주문에는 여러개의 상품이 있을수 있다 (N : M)
    - 다 : 다 관계의 경우 1 : N  과 N : 1 관계로 풀어줘야 한다
    - 따라서 주문상품이라는 엔티티를 둔다
    - 하나의 주문에는 여러개의 주문상품이 담길 수 있다 (1 : N)
    - 하나의 상품이 여러개의 주문상품에 담길수 있다 (1 : N)
- 상품의 종류에는 도서 , 음반 , 영화 가 있는데 상품을 상속받는다
- 하나의 주문에는 하나의 배송 정보가 담긴다 (1 : 1)
- 여러개의 상품은 여러개의 카테고리에 속할 수 있다 (N: M)
    - 실무에서 다 : 다 관계를 사용하는 것은 좋지 않다

### 회원 엔티티 분석
![jpa_shop_엔티티 설계](https://user-images.githubusercontent.com/42866800/164470893-f95348f4-dc77-4f68-b1a3-7a4986ba077e.png)

- Member 엔티티는 Address를 갖는다
- Address는 Enmbeded 타입 값 타입이다
- 회원이 여러개의 주문을 갖는다는 가정하에 , Member가 orders를 List로 갖는다

- Order와 Item은 N : M 관계이다
- N : M 관계를 해체하기 위해 OrderItem 엔티티를 뒀다
- 여러개의 주문 상품이 하나의 주문에 담긴다
- 따라서 Order를 Fk로 갖고있는 쪽이 연관관계의 주인이 된다
- 하나의 상품이 여러개의 주문에 담길수 있다
- 따라서 Item을 Fk로 갖고있는 쪽이 연관관계의 주인이 된다

- 여러개의 아이템이 여러개의 카테고리에 속할 수 있다
- N : M 관계이다

- 하나의 주문은 하나의 배송정보를 갖는다
- 1 : 1 관계이다
- 배송 엔티티의 경우 Address를 임베디드 타입으로 갖는다

### 테이블 분석
![jpa_shop_테이블 설계도](https://user-images.githubusercontent.com/42866800/164470916-4993d63c-4cda-4370-8f36-c64850746175.png)

- 하나의 회원이 여러개의 주문을 가지므로 Orders 테이블이 Member를 FK로 갖는다

- 하나의 주문은 여러개의 주문상품을 가지므로 Order_item 테이블이 Order를 FK로 갖는다
- FK Order가 연관관계의 주인이 된다

- 하나의 아이템은 여러개의 주문상품에 담길수 있다
- 따라서 Order_item 테이블이 Item을 FK로 갖는다
- FK Item이 연관관계의 주인이 된다

- 여러개의 아이템이 여러 카테고리에 속할 수 있다
- 디비에서는 N : M 관계를 표현할수 없다
- 따라서 이를 해체해주기 위해 category_item 테이블을 뒀다
- 하나의 아이템은 여러개의 category아이템에 속할 수 있다
- category_item 테이블이 item 을 FK로 갖는다
- 하나의 카테고리에는 여러개의 카테고리아이템이 있을 수 있다
- 따라서 category_item 테이블이 category를 fk로 갖는다
- category fk가 연관관계의 주인이 된다
-----------------------------------------------------------------

# 엔티티 상속 관계
## @Inheritance
- 상속관계를 표현하기 위해 @Inheritance 어노테이션을 사용한다
- `@Inheritance(strategy = InheritanceType.*SINGLE_TABLE*)` 하면 모든 자식 엔티티가 하나의 테이블로 합쳐져서 생성 된다
- 하나의 테이블에 자식 엔티티의 모든 컬럼이 들어있기 때문에 조회시 성능면에서 유리하다
- 단 , 단점은 자식 엔티티의 모든 컬럼은 null을 허용해야 한다
- 단일 테이블이 커지는 경우 조회 성능이 저하될 수 있다
- 하나의 테이블에 Book , Albun , Movie 엔티티에 있는 컬럼이 전부 들어간다
- 모든 컬럼은 null을 허용해야 한다

## @DiscriminatorColumn
- `@DiscriminatorColumn(name = "dtype")`
- 부모 클래스에 선언한다
- 하위 클래스를 구분하기 위해 사용한다
- default 는 DTYPE이다
- 상속관계 매핑시 SINGLE_TABLE 인 경우 모든 자식 엔티티의 컬럼이 한테이블에 모아진다
- 그러면 어떤 자식인지 구분하기 위해 DTYPE을 사용한다
- 즉 서브 엔티티를 구분하고 명시하기 위해 사용한다
- SINGLE_TABLE 전략의 경우 무조건 DTYPE 칼럼이 생긴다

## @DiscriminatorValue
- `@DiscriminatorValue("M")`
- 예) Movie 엔티티에 해당하는 컬럼에 데이터가 저장되는 경우 해당 데이터가 Movie 엔티티 소속 이라는 것을 구분하기 위해 DTYPE  컬럼에 A가 저장된다



### 데이터 중심 설계의 문제점

- 외래키 식별자를 객체에서 직접 다루게 된다
- 조회를 하는 경우 select 쿼리를 한번더 날려야 한다
- 왜?
- 연관관계가 없기 때문이다
- 객체지향 스럽지 않은 방식이다
- 객체를 테이블에 맞춰 모델링하면 협력관계를 만들수 없다

- 테이블은 외래키를 사용하여 여러 테이블을 조인하여 연관된 테이블를 찾는다
- 객체는 참조를 사용하여 연관된 객체를 찾는다

### 연관관계 주인 설정하는 방법
- 연관관계를 매핑하는 어노테이션은 데이터베이스와 관계있다
- 연관관계의 주인만이 외래 키를 관리할 수 있다
- 주인이 아닌 경우 mappedBy 속성으로 주인을 지정해 준다
- 외래키가 있는 곳을 주인으로 정해야 한다
- mappedBy는 OneToMany 쪽에 건다 (가짜 매핑 - 읽기만 가능)
- Many 쪽에 있는 참조값이 연관관계의 주인이 된다

## 엔티티 클래스 분석 1

### Member 엔티티

```java
@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList();

}
```

### Member 엔티티와 Address 관계

- `@Embeddable`
- 서로 관련있는 필드를 하나의 엔티티로 묶어 관리한다
- Address 는 city , street , zipcode가 필요하다
- 얘내를 전부 String으로 만들면 관리하기 힘들다
- 따라서 Address 라는 클래스를 만들고 안에 city , street , zipcode 필드를 넣는다
- 해당 엔티티가 필요한곳에서 @Embedded를 사용하여 Member 엔티티가 Address 엔티티를 포함하고 있다고 선언한다

```java
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;
}
```

```java
@Entity
@Getter
@Setter
public class Member {

    ...

    @Embedded
    private Address address;

    ...

}
```

### Member 엔티티와 Order 엔티티 관계

- 여러번의 주문이 한명의 회원에 의해 이뤄질 수 있다
- 따라서 Order 입장에서 ManyToOne 관계이다
- 테이블 설계에서 member fk를 Order가 갖기 때문에
- Order 엔티티에 선언된 Member 필드가 관계의 주인이 된다
- 따라서 Order 엔티티와 Member 엔티티가 조인 할 때 member_id 컬럼을 사용하여 조인한다

```java
@ManyToOne
@JoinColumn(name = "member_id")
private Member member;
```

- 한명의 회원이 주문을 여러번 할 수 있다
- 따라서 Member 입장에서 OneToMany 관계이다
- Member 엔티티의 orders 필드 입장에서 봤을때는 관계의 주인이 member 이다
- 따라서 mappedBy 를 사용하여 관계의 주인을 표시해 준다

```java
@OneToMany(mappedBy = "member")
private List<Order> orders = new ArrayList();
```

### Order 엔티티와 OrderItem 엔티티 관계

- 여러개의 주문상품이 하나의 주문에 담길 수 있다
- 따라서 OrderItem 입장에서 ManyToOne 관계이다
- OrderItem 테이블이 Order를 fk로 갖기 때문에 OrderItem 엔티티의 Order 필드가 관계의 주인이 된다
- 따라서 조인시 order_id를 사용하여 조인한다

```java
@ManyToOne
@JoinColumn(name = "order_id")
private Order order;
```

- 하나의 주문에 여러 주문상품이 담길 수 있다
- 따라서 Order 입자에서 OneToMany 관계이다
- OrderItem 엔티티의 Order 필드가 관계의 주인이므로 Order 엔티티의 OrderItem 필드에는 mappedBy를 사용하여 관계의 주인을 적어준다

```java
@OneToMany(mappedBy = "order")
private List<OrderItem> orderItems = new ArrayList<>();
```

### OrderItem 엔티티와 Item 엔티티 관계

- 여러개의 주문정보에 하나의 아이템이 들어 갈 수 있다
- 따라서 OrderItem 입장에서 ManyToOne 관계이다
- OrderItem 테이블이 Item fk를 갖고 있다
- OrderItem 엔티티의 item 필드가 관계의 주인이 된다

```java
@ManyToOne
@JoinColumn(name = "item_id")
private Item item;
```

- 아이템의 종류로 Book , Album , Movie가 있다
- 따라서 Item 엔티티를 추상클래스로 선언했다
- Book , Album , Movie 가 공통으로 name, price , stockQuantity 필드를 갖는다
- 상속관계를 표현하기 위해 @Inheritance 어노테이션을 사용했다
- `@Inheritance(strategy = InheritanceType.*SINGLE_TABLE*)` 하면 모든 자식 엔티티가 하나의 테이블로 합쳐져서 생성 된다
- 하나의 테이블에 자식 엔티티의 모든 컬럼이 들어있기 때문에 조회시 성능면에서 유리하다
- 단 , 단점은 자식 엔티티의 모든 컬럼은 null을 허용해야 한다
- 단일 테이블이 커지는 경우 조회 성능이 저하될 수 있다
- 하나의 테이블에 Book , Albun , Movie 엔티티에 있는 컬럼이 전부 들어간다
- 모든 컬럼은 null을 허용해야 한다

- `@DiscriminatorColumn(name = "dtype")`
- 부모 클래스에 선언한다
- 하위 클래스를 구분하기 위해 사용한다
- default 는 DTYPE이다
- 상속관계 매핑시 SINGLE_TABLE 인 경우 모든 자식 엔티티의 컬럼이 한테이블에 모아진다
- 그러면 어떤 자식인지 구분하기 위해 DTYPE을 사용한다
- 즉 서브 엔티티를 구분하고 명시하기 위해 사용한다
- SINGLE_TABLE 전략의 경우 무조건 DTYPE 칼럼이 생긴다

- `@DiscriminatorValue("M")`
- 예) Movie 엔티티에 해당하는 컬럼에 데이터가 저장되는 경우 해당 데이터가 Movie 엔티티 소속 이라는 것을 구분하기 위해 DTYPE  컬럼에 A가 저장된다

### Order 엔티티와 Delivery 엔티티 관계

- 하나의 배송은 하나의 주문을 갖는다
- 따라서 Order와 Delivery는 OneToOne 관계이다
- Delivery 엔티티의 Order 필드 입장에서 관계의 주인이 Order 엔티티의 delivery 필드이기 때문에
- mappedBy를 사용하여 관계의 주인 필드를 적어준다

```java
@OneToOne(mappedBy = "delivery")
private Order order;
```

- 하나의 주문은 하나의 배송을 갖는다
- Order 입장에서 Delivery와 OneToOne 관계이다
- Order 테이블이 Delivery fk를 갖고있기 때문에
- Order 엔티티의 Deliver 필드가 관계의 주인이 된다
- 따라서 두 엔티티 조인시 delivery_id 컬럼을 사용한다

```java
@OneToOne
@JoinColumn(name = "delivery_id")
private Delivery delivery;
```

### Order 엔티티와 OrderStatus Enum 관계

- 주문은 상태를 갖는다
- 주문완료와 주문취소 상태를 갖는다
- Order 엔티티가 OrderStatus를 갖고 있다

```java
@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

		...
		private OrderStatus status; // 주문 상태
}
  
```

```java
public enum OrderStatus {
    ORDER , CANCEL
}
```

## Category와 Item 사이 관계
- 여러개의 Category에는 여러개의 Item이 속할 수 있다
- 여러개의 Item이 여러개의 Category에 속할 수 있다
- 다 : 다 관계이다

- 엔티티 사이에 중간 테이블을 둬서 1 : 다 와 다 : 1 관계로 분리한다
- Category 엔티티와 Item 엔티티사이에 category_item 이라는 테이블을 만든다
- 해당 테이블을 각 엔티티의 FK 만을 관리한다
- 단점
- - 관리해야 하는 테이블이 늘어난다
- - 테이블에 컬럼을 추가할 수 없다

```java
@ManyToMany
@JoinTable(name = "category_item", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
private List<Item> items = new ArrayList<>();
```

- Category 엔티티를 기준으로 조인시 참조할 FK가 category_id 컬럼이다
- Item 엔티티를 기준으로 조인하기 위해 참조할 FK가 item_id 컬럼이다

- @ManyToMany의 반대쪽
- Item 엔티티 관점에서 Category 엔티티를 바라볼때
- 관계의 주인은 Category 엔티티에 선언된 인스턴스 필드가 주인이 된다
- 따라서 mappedBy를 사용하여 items를 관계의 주인으로 설정해 준다

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {

    ...

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}
```

- 카테고리 엔티티의 계층관계 표현
- 여러개의 Category가 하나의 부모 카테고리에 속할 수 있다
- 하나의 Category가 여러개의 자식 카테고리를 가질 수 있다

```java
@ManyToOne
@JoinColumn(name = "parent_id")
private Category parent;

@OneToMany(mappedBy = "parent")
private List<Category> child = new ArrayList<>();
```

## 엔티티 설계시 주의할 점
- 엔티티에서 setter 메서드를 쓰면 좋지 않다
- ArrayList 객체는 필드에서 초기화 해주고 한번 생성되면 변경하지 않는다

### EAGER 와 LAZY

- 모든 연관관계는 다 지연로딩을 설정한다
- @ManyToOne 매핑관계에서 default가 EAGER이다
- 조회시 N + 1 문제가 발생할 수 있다


### CASCADE

- 엔티티의 상태 변화를 전파시킨다
- 엔티티의 변화가 발생하면 연관되어 있는 엔티티에도 상태변화를 반영해 준다
- default는 전이 시키지 않는것이다

```java
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
private List<OrderItem> orderItems = new ArrayList<>();

@OneToOne(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
@JoinColumn(name = "delivery_id")
private Delivery delivery;
```

- order 엔티티에 데이터 변경이 일어나면 연관된 OrderItems와 delivery 엔티티에도 Order 엔티티의 상태 변화를 반영해 준다


### 연관관계 편의 메서드

- 하나의 메서드를 통해 서로 관계가 있는 객체를 설정해 준다

```java
public class Order {
    ...

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

   ...

    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
}
```

- Order와 Member가 서로 양방향 관계이다
- setMember 메서드를 통해 물리적 양방향 관계를 설정해 준다
- 한명의 회원이 주문을 여러개 생성할 수 있으므로 회원이 주문을 할때마다
- Order 객체를 `orders` ArrayList에 담아준다
- 어노테이션을 통해 테이블을 기준으로 객체를 매핑시켜 줬다면
- 실제 물리적으로 객체끼리 연관관계를 맺어준다
- 순수한 객체 상태에서도 정상적으로 연관관계를 동작시키기 위해 사용한다
- 따라서 하나의 메서드 안에서 객체의 연관관계를 맺어준다
- 메서드의 위치는 양방향관계에서 주인쪽에 둔다

# 변경 감지와 merge
## 준영속 엔티티
- 영속성 컨텍스트가 더이상 관리하지 않는 엔티티
- DB를 한번 거친 데이터
## 더티체킹
- Transaction 안에서 엔티티의 변경이 일어나면
- 변경 내용을 자동으로 데이터베이스에 반영
- 즉 , 변경을 감지해서 DB에 반영한다

## 데이터베이스에 변경 데이터를 저장하는 시점
1. Transaction Commit
2. EntityManage Flush
3. JPQL 사용

<aside>
📌 트랜잭션이 끝나는 시점에 변화가 있는 모든 엔티티 객체를 데이터베이스에 자동으로 반영해 준다

</aside>

- 엔티티를 조회하면 해당 엔티티의 조회 상태 그대로 스냅샷을 만든다
- 트랜잭션이 끝나는 시점에 스냅샷과 현재의 상태를 비교해 다른점이 있는 경우 Update Query를 데이터베이스로 전달한다

# 기능 구현하면서 발생했던 에러
## 상품 주문시 service 메서드에 @Transactional 어노테이션을 붙이지 않아 디비에 주문 내역이 저장되지 않았다
- 엔티티 매니저를 사용하는 경우 메서드 실행이 완료된후 실제 데이터가 디비에 반영된다
- @Transactonal에 의해 커밋하는 순간 영속성 컨텍스트에 새로 저장된 엔티티를 데이터베이스에 반영한다
- commit 될때 JPA가 엔티티의 변경을 감지하게 된다
- 변경이 발생하지 않은 경우 save 메서드를 호출하여 insert 쿼리를 실행시킨다
- 변경이 발생한 경우 merge 메서드를 호출하여 update 쿼리를 실행시킨다
- merge의 경우 엔티티의 부분만을 변경할 수 없다
- 따라서 수정을 하는 경우
- db에서 엔티티를 한번 조회해 온다
- 그러면 엔티티가 반영속 상태가 된다
- 이 상태에서 엔티티의 데이터를 수정한다
- 엔티티의 id는 변하지 않기 때문에 JPA가 id를 사용하여 변경을 감지하여 해당 row의 변경된 부분만 update 쿼리를 실행해 준다