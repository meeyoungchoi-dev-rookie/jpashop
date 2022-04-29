package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager entityManager;

    public void save(Item item) {
        // 새로 생성한 객체인 경우
        if (item.getId() == null) {
            entityManager.persist(item);
        } else {
            entityManager.merge(item);
            // JPA가 자동으로 db에서 row를 찾아 파라미터로 전달된 item으로 바꿔치기 해준다
        }
    }


    public Item findOne(Long id) {
        return entityManager.find(Item.class, id);
    }

    public List<Item> findAll() {
        return entityManager.createQuery("select i from Item i", Item.class).getResultList();
    }
}
