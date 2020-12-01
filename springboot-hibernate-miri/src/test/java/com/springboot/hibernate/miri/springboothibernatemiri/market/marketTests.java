// package com.springboot.hibernate.miri.springboothibernatemiri.market;

// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.Arrays;

// import javax.transaction.Transactional;

// import com.springboot.hibernate.miri.springboothibernatemiri.repository.ItemRepository;
// import com.springboot.hibernate.miri.springboothibernatemiri.repository.OrderItemRepository;
// import com.springboot.hibernate.miri.springboothibernatemiri.repository.OrderRepository;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.annotation.Rollback;

// import lombok.extern.log4j.Log4j2;

// @Transactional
// @Rollback(false)
// @SpringBootTest
// @Log4j2
// public class marketTests {
//     @Autowired
//     OrderRepository orderRepository;
    
//     @Autowired
//     OrderItemRepository orderItemRepository;
    
//     @Autowired
//     ItemRepository itemRepository;
    
//     @Test
//     public void init(){
//         log.info("hello world!");
//         /**
//          *  [예외] org.hibernate.MappingException: Could not determine type for: com.springboot.hibernate.miri.springboothibernatemiri.market.Item, at table: tbl_order_item, for columns: [org.hibernate.mapping.Column(item)]
//          * 
//          *  [원인]
//          *  OrderItem 의 필드 Item 에 어노테이션 깜박한 것이 원인.
//             @ManyToOne(fetch = FetchType.LAZY)
//             @JoinColumn(name = "id")
//             private Item item;
//          *
//          * 
//          * [예외]
//          * org.hibernate.MappingException: Repeated column in mapping for entity: com.springboot.hibernate.miri.springboothibernatemiri.market.OrderItem column: id (should be mapped with insert="false" update="false")
//          * 
//          * [가정]
//          * private Order order
//          * private Item item 각 필드에 @JoinColumn("id") 가 들어가 "id" 부분이 중복되어 인식하지 못하는 경우인가?
//          * [원인]
//          * 위의 가정이 맞았음.
//          * 
//          */
//     }

//     @Test
//     public void repositoryTest(){
//         // spring data jpa
//         /**
//          * [시나리오]
//          * item -> orderItem -> order 를 초기화하고,
//          * 한꺼번에 repository.save(n); 한다.
//          */

//         // 1. item 초기화
//         Item item = Item.builder()
//                     .name("item1")
//                     .price(BigDecimal.valueOf(1000L))
//                     .quantity(10)
//                     .build();
//         Item item2 = Item.builder()
//                     .name("item2")
//                     .price(BigDecimal.valueOf(5000L))
//                     .quantity(10)
//                     .build();
//         log.info("===item.toString():\n"+item.toString());
//         log.info("===item2.toString():\n"+item2.toString());

//         // 2. orderItem 초기화
//         OrderItem orderItem = OrderItem.builder()
//                             // .order(order) // 3.order 초기화 후 넣을 예정
//                             .item(item)
//                             .quantity(2)
//                             .price(BigDecimal.ZERO)// getTotalPrice 로 산출
//                             .build();
//         OrderItem orderItem2 = OrderItem.builder()
//                             // .order(order) // 3.order 초기화 후 넣을 예정
//                             .item(item2)
//                             .quantity(2)
//                             .price(BigDecimal.ZERO) // getTotalPrice 로 산출
//                             .build();
        
//         // 2-1.orderItem 의 주문 금액 계산하여 할당
//         orderItem.getTotalPrice();
//         orderItem2.getTotalPrice();

        
//         // 3. Order 초기화 후 save
//         Order order = Order.builder()
//                     .orderStatus(true)
//                     .orderDate(LocalDateTime.now())
//                     .member("member")
//                     .orderItems(Arrays.asList(orderItem,orderItem2))
//                     .price(BigDecimal.ZERO) // getTotalPrice
//                     .build();

//         // 3-1. order 안에 있는 orderItems 의 가격 다시 측정해서 계산.
//         order.getTotalPrice();
        
//         log.info("===order.toString():\n"+order);

//         // 3-2. Order 를 각 OrderItem.order 에 할당
//         order.changeOrder();



//         order.getOrderItems().forEach(i->{
//             // log.info("order.changeOrder 이후 OrderItem.getOrder forEach:\n"+i.getOrder().toString());
//             assertTrue(order.toString().equals(i.getOrder().toString()));
//         });
        
//         log.info("===orderItem.toString():\n"+orderItem);
//         log.info("===orderItem2.toString():\n"+orderItem2);

//         // Order, OrderItem, Item 을 save 하기
//         orderRepository.save(order); // db 에 들어감 : order 저장

//         order.getOrderItems().forEach(i->{ // db 안들어감 : orderitme 저장
//             orderItemRepository.save(i);
//         });

//         itemRepository.save(item); // db 안들어감 // item 저장

//         /**
//          * [예외]
//          * org.springframework.dao.InvalidDataAccessApiUsageException: org.hibernate.TransientPropertyValueException: 
//          * object references an unsaved transient instance - save the transient instance before flushing : 
//          * 야, 너 transient 객체를 영속화하기 전에 얘를.. 세이브해야 할거 아냐...!!!
//          * 
//          * [내가 받아들인 해석]
//          * 야. item 을 먼저 세이브해야.. 아이디가 생기니까.. orderItem 을 세이브할 수 있을거 아니야....
//          * 
//          * com.springboot.hibernate.miri.springboothibernatemiri.market.OrderItem.item -> com.springboot.hibernate.miri.springboothibernatemiri.market.Item; 
//          * nested exception is java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: object references an unsaved transient instance - save the transient instance before flushing : com.springboot.hibernate.miri.springboothibernatemiri.market.OrderItem.item -> com.springboot.hibernate.miri.springboothibernatemiri.market.Item
//          * 
//          * [가정]
//          * 현재 Order 는 save 되었음.
//          * 다만 orderItemRepository.save(i).. 을 호출할 때, 현재 OrderItem.item 의 id 가 null인 상태.
//          * 
//          * => cascade 적용 후 해결
//          *  @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
//             @JoinColumn(name = "item_id")
//             private Item item;
//          * => 말 그대로 계단처럼 타고나가듯, OrderItem 의 Item 을 먼저 저장한다.
//          * => marketTest-repositoryTest.txt 참고
//          * 
//          * 
//          * [가정2]
//          * 로그 마지막에 item select 가 찍힌다. 이미 orderItem 인서트 시 cascade 속성으로 인해 orderItem 안에 있는 item 을 이미 삽입했기 때문일까?
//          * 
//          * [원인]
//          * cascade 로 해결되었으나, CRUD 각 경우를 테스트하지 않고서 cascade 를 사용하면 의도와 달리 작용할 것 같음.
//          * https://thorben-janssen.com/avoid-cascadetype-delete-many-assocations/ : delete 의 경우
//          * 
//          */
//     }

//     @Test
//     public void repositoryTest2(){
//         /**
//          * repositoryTest 이어서 그대로 테스트하되, 코드 흐름을 보다 깔끔히 변경했으며 cascade 사용 X
//          */
//         // 1. item 초기화, 저장
//         Item item = Item.builder().name("item1").price(BigDecimal.valueOf(1000L)).quantity(10).build();
//         Item item2 = Item.builder().name("item2").price(BigDecimal.valueOf(5000L)).quantity(10).build();
        
//         Item itemSaved = itemRepository.save(item);
//         Item item2Saved = itemRepository.save(item2);
//         log.info("===itemSaved.toString():\n"+item.toString());
//         log.info("===item2Saved.toString():\n"+item2.toString());

//         // 2. orderItem 초기화, 저장
//         // orderItem.order == null => 3번에서 해결
//         // orderItem.price => getTotalPrice() 로 할당
//         OrderItem orderItem = OrderItem.builder().item(itemSaved).quantity(2).price(BigDecimal.ZERO).build();
//         OrderItem orderItem2 = OrderItem.builder().item(item2Saved).quantity(2).price(BigDecimal.ZERO).build();

//         orderItem.getTotalPrice();
//         orderItem2.getTotalPrice();

//         OrderItem orderItemSaved = orderItemRepository.save(orderItem);
//         OrderItem orderItemSaved2 =orderItemRepository.save(orderItem2);
//         log.info("==orderItemSaved.toString(): "+orderItemSaved.toString());
//         log.info("==orderItemSaved2.toString(): "+orderItemSaved2.toString());


//         // 3. Order 초기화, 저장
//         // orderItem.order 할당
//         Order order = Order.builder().orderStatus(true).orderDate(LocalDateTime.now()).member("member").orderItems(Arrays.asList(orderItemSaved,orderItemSaved2)).price(BigDecimal.ZERO).build();

//         order.changeOrder(); // order.orderItems 의 order 필드에 자기 자신 order 객체 할당
//         order.getTotalPrice(); // order.orderItems 의 주문 금액을 총합하여 order.price 에 할당 

//         Order orderSaved = orderRepository.save(order);
//         log.info("=== orderSaved.toString(): "+orderSaved.toString());
        
//     }

    
//     // @Test
//     // public void itemNewInstanceTest(){
//     //     /**
//     //      * item 을 maker 로 초기화하고, 디비에 저장한 다음, 다시 조회하기
//     //      * 
//     //      * [서비스단으로 옮길 때의 규칙]
//     //      * Item.id 값이 -1  => insert
//     //      * id 값이 1 이상 양수 => update.
//     //      */
//     //     // 1. 초기화
//     //     Item item = Item.maker(null, "item1", null, -5); // id X
//     //     Item item2 = Item.maker(-4L, "item2", null, -5); // id X

//     //     // 2. 서비스단 : 디비 저장
//     //     List<Long> idList = new ArrayList<>(); // 저장 후 발급되는 Item.id 보관하는 리스트
//     //     Arrays.asList(item,item2).forEach(i -> {
//     //         log.info("item.toString(): \n"+i.toString());

//     //         idList.add(itemRepository.save(i).getId());
//     //     });

//     //     // 3. 다시 가져와서, id 와 price 확인하기
//     //     /**
//     //      * org.hibernate.LazyInitializationException: could not initialize proxy [com.springboot.hibernate.miri.springboothibernatemiri.market.Item#387] - no Session
//     //      */
//     //     idList.forEach(i->{
//     //         Item itemSaved = itemRepository.getOne(i);
//     //         Long id = itemSaved.getId();
//     //         assertTrue( id != null && id >= 1 ); // id 는 null 이면 안되고, 1 이상이여야 한다.

//     //         // 0 이상 : compareTo 결과가 0(서로 같다) 혹은 1(인자보다 크다)
//     //         int result = itemSaved.getPrice().compareTo(BigDecimal.ZERO);
//     //         assertTrue( result <= 0 );

//     //         log.info("itemSaved.toString(): "+itemSaved.toString());

//     //     });


//     // }

//     // @Test
//     // public void orderNewInstanceTest(){
//     //     /**
//     //      * item 이 있을 때,
//     //      * orderItem 과 Order 를 디비에 저장하고
//     //      * 꺼내와 값 확인하기
//     //      * (maker 생성자 사용)
//     //      */
        
//     //     // 1. 기존에 저장된 item 을 가져온다.
//     //     Long targetId = 382L; // 경우에 따라 바뀌는 값

//     //     // 어.. 기존 객체 가져올 때 / 기존에 없는 객체 만들 때 분기점 필요.
//     //     itemRepository.
//     //     Item.maker(targetId, name, price, quantity)
//     //     // 2. OrderItem 을 초기화한다.

//     //     // 3. Order 을 초기화 및 저장한다.
//     // }
// }
