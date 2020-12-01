package com.springboot.hibernate.miri.springboothibernatemiri.market;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        log.info("=== start Arrays.asList(item,item2).forEach ...");
        List<Long> itemIdList = new ArrayList<>(); // repository.save() 반환 객체 저장용
        Arrays.asList(item,item2).forEach(i -> {
            // if(i.getId() == -1L){
            //     log.info("=== insert item!"); 
            // } else {
            //     log.info("=== update item!");
            // }

            itemIdList.add(itemRepository.save(i).getId());
        });
        log.info("=== end Arrays.asList(item,item2).forEach ..."); 

        // 1-2. 저장한 item 다시 조회하기
        List<Item> selectedItemList = new ArrayList<>(); // 다시 조회한 item 객체 저장용
        itemIdList.forEach(i->{
            selectedItemList.add(itemRepository.findById(i).get());
        });
        log.info("=== start selectedItemList.forEach : 저장한 item 다시 조회 ...");
        selectedItemList.forEach(i->{
            log.info(i.toString());
        });
        log.info("=== end selectedItemList.forEach : 저장한 item 다시 조회 ...");



        // 2. OrderItem 초기화 
        // Order 할당 : Order.id => null 
        // Item 할당 :  앞의 Item 객체 그대로 할당
        OrderItem orderItem = OrderItem.doMake(null, null, selectedItemList.get(0), 2, null); // order.id = -1, order.price = 0
        OrderItem orderItem2 = OrderItem.doMake(null, null, selectedItemList.get(1), 2, BigDecimal.valueOf(5000L)); // order.id = -1, order.price = 0

        // 2-1. orderItem.order 필드는 현재 null (다만 nullPointerException 방지하고자 id=-1 로 명시한 Order 객체넣음)
        // 2-2. Order 안에 orderItems 를 넣고, 디비에 저장
        // 이 때 Order -> OrderItem 순으로 저장한다.

        // 3. Order 초기화
        // Order.orderItems => 빈 리스트 할당
        Order order = Order.doMake(null, false, LocalDateTime.now(), "member", Arrays.asList(orderItem,orderItem2), null);

        log.info("== start order.toString: "+order.toString());
        order.getOrderItems().forEach(i->{
            log.info((i.getOrder()==null)?"OrderItem.order is null":"OrderItem.order is not null");
            log.info((i.getItem()==null)?"OrderItem.item is null":"OrderItem.item is not null");
        });
        log.info("== end order.toString");

        // 4. Order.changeOrder() 호출 : Order.orderItems 에게 Order 자신의 객체 넘겨줌
        // Order.changeOrder() 내부에서 OrderItem.changeOrder(Order order) 호출 : 자신이 속한 Order 객체를 받아, 자기 자신 OrderItem 의 order 필드에 할당.
        order.changeOrder();

        log.info("== log order.toString() after changeOrder(): "+order.toString());
        order.getOrderItems().forEach(i->{
            log.info((i.getOrder()==null)?"OrderItem.order is null":"OrderItem.order is not null");
            log.info((i.getItem()==null)?"OrderItem.item is null":"OrderItem.item is not null");
        });

        // 5. Order 저장
        Long orderId = orderRepository.save(order).getId();
        log.info("== orderId after save: "+orderId);

        // 6. OrderItem 저장
        order.getOrderItems().forEach(i -> {
            log.info("== orderItem Id after save: "+orderItemRepository.save(i).getId());
        });

        // 7. Order 다시 꺼내서 조회.
        Order selectedOrder = orderRepository.findById(orderId).get();
        log.info("== selectedOrder.toString() : "+selectedOrder.toString());
        selectedOrder.getOrderItems().forEach(i->{
            log.info("== selectedOrder.getOrderItems.toString() : "+i.toString());
            log.info("== selectedOrder.getOrderItems.i.getOrder.toString() : "+i.getOrder().toString());
        });



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
