package com.springboot.hibernate.miri.springboothibernatemiri.market;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * OrderItem
 * 
 * by miri
 * 
 * 주문에 포함된 상품에 대한 정보를 나타냄.
 * 
 * 멤버
 * - id, sn
 * - Item item : 상품. OrderItem 다:일 Item
 * - Order order : 주문 상품이 속한 주문. OrderItem 다:일 Order
 * - int quantity : 주문 수량.
 * - BigDecimal price : [프리징 데이터] 상품 단가 * 주문시 수량 곱한 최종 금액.
 * - getTotalPrice() : [프리징 데이터] 해당 OrderItem의 실제 주문 가격을 OrderItem.price 에 저장. 
 * -
 */

@Table(name = "tbl_order_item")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "item")
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;
    
    // private String sn;
    
    // 주문 => OrderItem 다:일 Order
    // eager 필드이므로 레이지 타입으로 가져오기
    // ... 이므로 Order 의 키를 매핑해와야 함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 상품 => OrderItem 다:일 Item
    // @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    // 주문 수량
    private int quantity;
    // [프리징 데이터] 상품 단가 * 주문 시 수량 곱해서 나온 주문 금액
    private BigDecimal price;

    @Builder
    public OrderItem(Order order, Item item, int quantity, BigDecimal price){
        this.order = order;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }


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

    /**
     * Order.changeOrder 호출 시 안에서 호출하는 메서드로
     * OrderItem.order 를 속하는 Order 객체로 할당한다.
     */
	public void changeOrder(Order order) {
        System.out.println("OrderItem.changeOrder() 호출");
        this.order = order;
    }
    
    /**
     * changeItem
     * orderItem.item 에 인자 item 을 할당한다.
     * item 할당 후에는 getTotalPrice 통해 이 OrderItem 의 최종 금액 산출한다.
     */
    public void changeItem(Item item){
        System.out.println("OrderItem.changeItem() 호출");
        this.item = item;
    }
}
