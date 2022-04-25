package jpabook.jpashop.relationexample;

import javax.persistence.*;

@Entity
public class Member2 {
    @Id
    @GeneratedValue
    private Long member_id;

    private String userName;

    // 여러명의 회원이 하나의 팀에 속할 수 있다
    @ManyToOne // 어떤 관계인가
    @JoinColumn(name = "team_id") // 관계를 만들기 위해 조인시켜야 하는 컬럼이 무엇인가
    private Team team;
}
