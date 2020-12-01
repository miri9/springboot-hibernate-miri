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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
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
 * 
 * - getTotalPrice() : [프리징 데이터] 해당 OrderItem의 실제 주문 가격을 OrderItem.price 에 저장. 
 * - changeOrder() : OrderItem.order 를 속하는 Order 객체로 할당한다.
 * - changeItem() : item 할당. 할당 후에는 getTotalPrice 통해 이 OrderItem 의 최종 금액 산출한다.
 */
@Table(name = "tbl_order_item")
@Entity
// @AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = "item")
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;
    
    // private String sn;
    
    // 주문 => OrderItem 다:일 Order
    // eager 필드이므로 레이지 타입으로 가져오기 + Order 의 키를 매핑해와야 함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 상품 => OrderItem 다:일 Item
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 주문 수량
    private int quantity;
    // [프리징 데이터] 상품 단가 * 주문 시 수량 곱해서 나온 주문 금액
    private BigDecimal price;

    /**
     * doMake
     * AllArgs 생성자를 빌더 패턴화 하여, 그로 인해 초기화된 객체 반환
     */
    public static OrderItem doMake(Long id,Order order, Item item, int quantity, BigDecimal price){
        return OrderItem.builder()
                .id(id)
                .order(order)
                .item(item)
                .quantity(quantity)
                .price(price)
                .build();
        
    }
    /**
     * AllArgs 생성자
     * - id == null 혹은 0이하 이면 id = -1 할당
     * - id != null 이면 id = 기존 id 할당
     * - price == null 이거나 price < 0 BigDecimal.ZERO 초기화
     * - quantity < 0 이면(음수) quantity 0 초기화
     * - TODO : order == null 이면? 나머지 order 필드 고정하되, order.id = -1 인 새 객체를 만든다.
     * - TODO : item == null 이면? 나머지 item 필드 고정하되, item.id = -1 인 새 객체를 만든다.
     */
    @Builder(access = AccessLevel.PROTECTED)
    public OrderItem(Long id,Order order, Item item, int quantity, BigDecimal price){
        this.id =  (id == null || id <= 0)? -1 : id;
        this.order = order == null ? ;
        this.item = item;
        this.quantity = quantity < 0 ? 0 : quantity;
        this.price = price == null || price.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : price;
    }


    /**
     * getTotalPrice
     * Item.price (단가) * OrderItem.quantity (주문 수량) 곱하여,
     * 실제 주문 금액을 산출해 OrderItem.price 에 저장.
     */
    public void getTotalPrice(){
        System.out.println("OrderItem.getotalPrice() 호출");

        this.price = this.item.getPrice()
                    .multiply(new BigDecimal(this.getItem().getQuantity()));

    }

    /**
     * changeOrder
     * Order.changeOrder 호출 시 안에서 호출하는 메서드로
     * OrderItem.order 를 속하는 Order 객체로 할당한다.
     */
	// public void changeOrder(Order order){
    //     System.out.println("OrderItem.changeOrder() 호출");
    //     this.order = order;
    // }
    
    // /**
    //  * changeItem
    //  * orderItem.item 에 인자 item 을 할당한다.
    //  * item 할당 후에는 getTotalPrice 통해 이 OrderItem 의 최종 금액 산출한다.
    //  */
    // public void changeItem(Item item){
    //     System.out.println("OrderItem.changeItem() 호출");
    //     // orderItem.item = item;
    // }
}
