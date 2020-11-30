package com.springboot.hibernate.miri.springboothibernatemiri.market;
/**
 * OrderItem
 * 
 * by miri
 * 
 * 주문에 포함된 상품에 대한 정보를 나타냄.
 * 
 * 멤버
 * - id, sn
 * - Item item : 상품
 * - Order order : 주문 상품이 속한 주문. OrderItem 다:일 Order
 * - int quantity : 주문 수량.
 * - BigDecimal price : [프리징 데이터] 상품 단가 * 주문시 수량 곱한 최종 금액.
 * - getTotalPrice() : [프리징 데이터] 해당 OrderItem의 실제 주문 가격을 OrderItem.price 에 저장. 
 */

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "tbl_orderItem")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "item")
public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;
    
    // private String sn;

    // 주문 => OrderItem 다:일 Order
    // eager 필드이므로 레이지 타입으로 가져오기
    // ... 이므로 Order 의 키를 매핑해와야 함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Order order;
    // 상품
    private Item item;
    // 주문 수량
    private int quantity;
    // [프리징 데이터] 상품 단가 * 주문 시 수량 곱해서 나온 주문 금액
    private BigDecimal price;

    /**
     * getTotalPrice
     * OrderItem.item.price (단가) * OrderItem.quantity (주문 수량) 곱하여,
     * 실제 주문 금액을 산출해 OrderItem.price 에 저장.
     */
    public void getTotalPrice(){
        System.out.println("OrderItem.getotalPrice() 호출");

        this.price = this.item.getPrice()
                    .multiply(new BigDecimal(this.getQuantity()));

    }
}
