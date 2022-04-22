package jpabook.jpashop.relationexample;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id
    @GeneratedValue
    private Long team_id;

    // 객체는 참조를 통해 연관관계가 설정된다
    // Team에서 member로 가려면 Team에도 member에 대한 참조를 넣어줘야 한다
    // Many쪽에 있는 FK 참조변수가 연관관계의 주인이 된다
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();
}
