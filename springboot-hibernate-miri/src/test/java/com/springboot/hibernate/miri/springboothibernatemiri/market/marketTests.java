package com.springboot.hibernate.miri.springboothibernatemiri.market;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import com.springboot.hibernate.miri.springboothibernatemiri.repository.ItemRepository;
import com.springboot.hibernate.miri.springboothibernatemiri.repository.OrderItemRepository;
import com.springboot.hibernate.miri.springboothibernatemiri.repository.OrderRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.log4j.Log4j2;

// @Transactional
// @Rollback
@SpringBootTest
@Log4j2
public class marketTests {
    @Autowired
    OrderRepository orderRepository;
    
    @Autowired
    OrderItemRepository orderItemRepository;
    
    @Autowired
    ItemRepository itemRepository;
    
    @Test
    public void init(){
        log.info("hello world!");
        /**
         *  [예외] org.hibernate.MappingException: Could not determine type for: com.springboot.hibernate.miri.springboothibernatemiri.market.Item, at table: tbl_order_item, for columns: [org.hibernate.mapping.Column(item)]
         * 
         *  [원인]
         *  OrderItem 의 필드 Item 에 어노테이션 깜박한 것이 원인.
            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "id")
            private Item item;
         *
         * 
         * [예외]
         * org.hibernate.MappingException: Repeated column in mapping for entity: com.springboot.hibernate.miri.springboothibernatemiri.market.OrderItem column: id (should be mapped with insert="false" update="false")
         * 
         * [가정]
         * private Order order
         * private Item item 각 필드에 @JoinColumn("id") 가 들어가 "id" 부분이 중복되어 인식하지 못하는 경우인가?
         * [원인]
         * 위의 가정이 맞았음.
         * 
         */
    }

    @Test
    public void repositoryTest(){
        // spring data jpa

        // 1. item 초기화
        Item item = Item.builder()
                    .name("item1")
                    .price(BigDecimal.valueOf(1000L))
                    .quantity(10)
                    .build();
        Item item2 = Item.builder()
                    .name("item2")
                    .price(BigDecimal.valueOf(5000L))
                    .quantity(10)
                    .build();
        log.info("===item.toString():\n"+item.toString());
        log.info("===item2.toString():\n"+item2.toString());

        // 2. orderItem 초기화
        OrderItem orderItem = OrderItem.builder()
                            // .order(order) // 3.order 초기화 후 넣을 예정
                            .item(item)
                            .quantity(2)
                            .price(BigDecimal.ZERO)// getTotalPrice 로 산출
                            .build();
        OrderItem orderItem2 = OrderItem.builder()
                            // .order(order) // 3.order 초기화 후 넣을 예정
                            .item(item2)
                            .quantity(2)
                            .price(BigDecimal.ZERO) // getTotalPrice 로 산출
                            .build();
        
        orderItem.getTotalPrice();
        orderItem2.getTotalPrice();

        
        // 3. Order 초기화 후 save
        Order order = Order.builder()
        .orderStatus(true)
        .orderDate(LocalDateTime.now())
        .member("member")
        .orderItems(Arrays.asList(orderItem,orderItem2))
        .price(BigDecimal.ZERO) // getTotalPrice
        .build();
        order.getTotalPrice();
        
        log.info("===order.toString():\n"+order);

        // Order 를 각 OrderItem.order 에 할당
        // Order , OrderItem.order 가 서로 같은지 확인
        order.changeOrder();
        order.getOrderItems().forEach(i->{
            // log.info("order.changeOrder 이후 OrderItem.getOrder forEach:\n"+i.getOrder().toString());
            assertTrue(order.toString().equals(i.getOrder().toString()));
        });
        
        log.info("===orderItem.toString():\n"+orderItem);
        log.info("===orderItem2.toString():\n"+orderItem2);

        // Order, OrderItem, Item 을 save 하기
        orderRepository.save(order);
        order.getOrderItems().forEach(i->{
            orderItemRepository.save(i);
        });
        // itemRepository.save(item);

        /**
         * [예외]
         * org.springframework.dao.InvalidDataAccessApiUsageException: org.hibernate.TransientPropertyValueException: object references an unsaved transient instance - save the transient instance before flushing : com.springboot.hibernate.miri.springboothibernatemiri.market.OrderItem.item -> com.springboot.hibernate.miri.springboothibernatemiri.market.Item; nested exception is java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: object references an unsaved transient instance - save the transient instance before flushing : com.springboot.hibernate.miri.springboothibernatemiri.market.OrderItem.item -> com.springboot.hibernate.miri.springboothibernatemiri.market.Item
         * 
         * [가정]
         * 현재 Order 는 save 되었음.
         * 다만 orderItemRepository.save(i).. 을 호출할 때, 현재 OrderItem.item 의 id 가 null인 상태.
         * 
         * => cascade 적용 후 해결
         *  @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
            @JoinColumn(name = "item_id")
            private Item item;
         * => 말 그대로 계단처럼 타고나가듯, OrderItem 의 Item 을 먼저 저장한다.
         * => marketTest-repositoryTest.txt 참고
         * 
         * 
         * [가정2]
         * 로그 마지막에 item select 가 찍힌다. 이미 orderItem 인서트 시 cascade 속성으로 인해 orderItem 안에 있는 item 을 이미 삽입했기 때문일까?
         * 
         * [원인]
         * 맞음.
         * 
         */

        
        
    }
}
