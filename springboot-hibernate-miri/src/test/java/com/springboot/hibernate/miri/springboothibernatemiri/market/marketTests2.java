package com.springboot.hibernate.miri.springboothibernatemiri.market;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.transaction.Transactional;

import com.springboot.hibernate.miri.springboothibernatemiri.repository.ItemRepository;
import com.springboot.hibernate.miri.springboothibernatemiri.repository.OrderItemRepository;
import com.springboot.hibernate.miri.springboothibernatemiri.repository.OrderRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import lombok.extern.log4j.Log4j2;

@Transactional
@Rollback(false)
@SpringBootTest
@Log4j2
public class marketTests2 {
    @Autowired
    OrderRepository orderRepository;
    
    @Autowired
    OrderItemRepository orderItemRepository;
    
    @Autowired
    ItemRepository itemRepository;
    
    /**
     * [예외1]
     * org.hibernate.MappingException: Could not determine type for: com.springboot.hibernate.miri.springboothibernatemiri.market.Item, at table: tbl_order_item, for columns: [org.hibernate.mapping.Column(item)]
     * 
     * tbl_order_item 테이블에서, Item의 타입을 결정할 수 없음.
     * 
     * [가정]
     * private Order order
     * private Item item 각 필드에 @JoinColumn(...) 이 없어 
     * 각 타입을 tbl_orderItem 의 어느 column 에 매핑해야 할지 모른다?
     * 
     * [원인]
     * 두 필드에 @JoinColumn("id") 을 추가하여 해결.
     * 
     * 
     * [예외2]
     * org.hibernate.MappingException: Repeated column in mapping for entity: com.springboot.hibernate.miri.springboothibernatemiri.market.OrderItem column: id (should be mapped with insert="false" update="false")
     * [예외2 원인]
     * OrderItem 필드 Item , Order 에 @Column("OOO") 부여함으로써 해결.
     * 
     * Order table 에서 아래의 두 컬럼은 id 로 표현되므로 중복된다.
     * 따라서 영속 영역에서 컬럼값을 다르게 해 줄 필요가 있다.
     * => 실제로 데이터를 가져올 때 조인이 이루어지는 필드명의 기준은 영속 영역의 이름.
     * 
     * 컬럼을 가져올 때 필드 타입, 영속 타입으로 가져올 수 있는데
     * @Colum 으로 영속 타입에 따로 값을 부여할 수 있다.
     * 
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id")
        private Order order; // PK 필드명 id

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "item_id")
        private Item item; // PK 필드명 id
     * 
     */
    @Test
    public void init(){
        log.info("hello world!");
    }

    /**
     * [시나리오]
     * - 캐스캐이드 등 JPA API 없이 , 순수 객체를 다루듯 접근하기
     * - 기존에 Item 이 있을 때, 새로 OrderItem-Order 가 만들어져 디비에 저장된다.
     */
    @Test
    public void OrderWithObjTest(){

        // 1. item 초기화 후 저장
        Item item = Item.doMake(null, "item1", BigDecimal.valueOf(2000l), 10);
        Item item2 = Item.doMake(null, "item2", BigDecimal.valueOf(1000l), 5);

        // 서비스 단에서 저장 : Item.id == -1 이면, 객체 없는 값이므로 insert
        Arrays.asList(item,item2).forEach(i -> {
            if(i.getId() == -1L){
                log.info("=== insert item!"); 
            } else {
                log.info("=== update item!");
            }

            itemRepository.save(i);
        });

        // 2. OrderItem 초기화 
        // Order 할당 : Order.id => null 에 준하는 값 (-1) 할당
        // Item 할당 :  앞의 Item
        OrderItem orderItem = OrderItem.doMake(null, Order.doMakeEmptyOne(), item, 2, null); // id = -1, price = 0
        OrderItem orderItem2 = OrderItem.doMake(null, Order.doMakeEmptyOne(), item2, 2, BigDecimal.valueOf(5000L)); // id = -1, price = 0

        // 2-1. orderItem.order 현재 값이 없는 상황.
        // => 현재 OrderItem.order 의 아이디는 -1

        // 2-3. (orderItem db 저장 생략)

        // 3. Order 초기화

        // 3-1. orderItem.order 에 Order 할당
        // 3-2. order에 orderItems 할당


        // 3-1. (order db 저장 생략)

    }
    /**
     * [시나리오]
     * - spring data jpa : cascade 이용
     * - 기존에 Item 이 있을 때, 새로 OrderItem-Order 가 만들어져 디비에 저장된다.
     * 
     */
    @Test
    public void OrderWithCascadeTest(){

    }

    /**
     * [시나리오]
     * - hibernate EntityManager
     * - 기존에 Item 이 있을 때, 새로 OrderItem-Order 가 만들어져 디비에 저장된다.
     * - 하이버네이트 쿼리와 실제 DB 로 들어가는 쿼리의 차이점?
     */
    @Test
    public void OrderWithEntityManagerTest(){

    }
}
