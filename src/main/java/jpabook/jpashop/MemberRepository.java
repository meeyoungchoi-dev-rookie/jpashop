package jpabook.jpashop;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


public class MemberRepository {
    // 스프링 부트가 EntityManager를 의존관계 주입해 준다
    @PersistenceContext
    private EntityManager em;

    // 엔티티 매니저를 통한 데이터 변경은 항상 트랜잭션 안에서 이루어져야 한다
    @Transactional
    public Long save(Member member) {
        em.persist(member);

        // 커맨드와 쿼리를 분리해야 한다 따라서 저장을 하고 난후 엔티티를 반환하지 않는다
        // 대신 ID 정보가 있으면 조회시 활용할 수 있기 때문에 ID만 반환해준다
        return member.getId();
    }

    @Transactional
    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
