package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    // Transactional 어노테이션이 테스트 케이스에 있으면 테스트가 끝난후 바로 롤백을 해버린다
    // 따라서 데이터베이스에 데이터가 반영되지 않는다
    // 왜?
    // 데이터가 들어가 있으면 반복적인 테스트를 할 수 없기 때문이다
    @Test
    @Transactional
    @Rollback(false)
    public void testMember() {
        // given
        Member member = new Member("memberA");
        Long id =  memberRepository.save(member);

        Member result = memberRepository.find(id);


        Assertions.assertThat(result.getId()).isEqualTo(member.getId());
        Assertions.assertThat(result.getUserName()).isEqualTo(member.getUserName());
        Assertions.assertThat(result).isEqualTo(member);
        // 왜?
        // 같은 트랜잭션 안에서 엔티티를 저장하고 조회하기 때문에 영속성이 같다
        // 영속성 컨텍스트 안에서는 ID 값이 같으면 같은 엔티티로 식별한다
        // 그래서 select 쿼리를 날리지 않고도 같은 영속성 컨텍스트 안에 있기 때문에 id를 사용해서 꺼내오게 된다
    }




}