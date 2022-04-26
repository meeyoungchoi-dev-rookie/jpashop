package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 재고수량 증가 로직
    // 엔티티 자체에서 해결할 수 있는 것은 엔티티 자체에 넣는것이 좋다
    // 데이터를 갖고 있는 쪽에 비즈니스 로직이 있는 것이 가장 좋다
    // 응집력이 높아진다
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }


    // 재고수량 감소 로직
    public void minusStock(int quantity) {
        int resultStock = this.stockQuantity -= quantity;
        if (resultStock < 0) {
            throw  new NotEnoughStockException("need more stock");
        }

        this.stockQuantity = resultStock;
    }
}
