package com.springboot.hibernate.miri.springboothibernatemiri.market;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Order
 * 
 * by miri
 * 
 * 주문 완료 시간, 상태, 주문자, 주문 상품들을 나타내는 클래스
 * 
 * 멤버
 * - id, sn
 * - String (추후 Enum화) orderStatus : 주문 상태
 * - LocalDateTime orderDate : 주문 완료 == 결제 완료 시각
 * - String (추후 Member화) member : 주문자
 * - List<OrderItem> orderItems : 주문한 상품. Order 1:다 OrderItem
 * 
 * - BigDecimal price : [프리징 데이터] 주문 완료 시점에서의 총 주문 금액. 즉 모든 orderItems 의 가격 총합.
 * - getTotalPrice() : [프리징 데이터] price 를 계산하여 Order.price 에 저장.
 * - 
 */

@Table(name = "tbl_order")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "orderItems")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // private String sn;

    // 주문 상태
    private boolean orderStatus;
    // 주문 완료 시각
    private LocalDateTime orderDate;
    // 주문자
    private String member;
    
    // 주문 상품 => Order 1:다 OrderItem
    // 레이지니까, 키를 바로 가져올 수 없으므로 미러링해와야 한다.
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    // [프리징 데이터] 주문 완료 시점에서의 총 주문 금액
    private BigDecimal price;

    @Builder
    public Order(boolean orderStatus, LocalDateTime orderDate, String member, List<OrderItem> orderItems, BigDecimal price){
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.member = member;
        this.orderItems = orderItems;
        this.price = price;
    }


    /**
     * getTotalPrice
     * Order.orderItems의 각 orderItem의 주문 금액을 "가져와",
     * Order.price 으로 sum하여 저장.
     */
    public void getTotalPrice(){
        System.out.println("Order.getotalPrice() 호출");
        
        for(OrderItem orderItem : this.orderItems){
            orderItem.getTotalPrice();
            
            this.price = this.price.add(orderItem.getPrice());
        }
    }

    /**
     * changeOrder
     * Order.orderItems 에게 Order 자신의 객체를 
     * OrderItem.order 에 할당한다. (OrderItem.changeOrder(this) 호출)
     */
    public void changeOrder(){
        System.out.println("Order.changeOrder() 호출");

        for(OrderItem orderItem : this.orderItems){
            orderItem.changeOrder(this);
        }
    }

}
