package com.springboot.hibernate.miri.springboothibernatemiri.market;

/**
 * Item
 * 
 * by miri
 * 
 * 상품의 이름, 단가, 재고 수량을 표기함
 * 
 * - id, sn
 * - String name : 상품명
 * - BigDecimal price : 현재 단가
 * - int quantity : 현재 재고로 남아있는 상품 수량
 * 
 * - static maker : 생성자 역할 스태틱 메서드.
 * 
 * [서비스단으로 옮길 때의 규칙]
 * Item.id 값이 -1  => insert
 * id 값이 1 이상 양수 => update.
 */

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tbl_item")
// @AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;
    
    // @GeneratedValue
    // private String sn;
    
    // 상품명
    private String name;
    // 상품 단가
    private BigDecimal price;
    // 상품 재고 수량
    private int quantity;
    
    
    /**
     * doMake
     * AllArgs 생성자를 빌더 패턴화 하여, 그로 인해 초기화된 객체 반환
     */
    public static Item doMake(Long id,String name, BigDecimal price, int quantity){
        return Item.builder()
                .id(id)
                .name(name)
                .price(price)
                .quantity(quantity)
                .build();
        
    }
    /**
     * AllArgs 생성자
     * - id == null 혹은 0이하 이면 id = -1 할당
     * - id != null 이면 id = 기존 id 할당
     * - price == null 이거나 price < 0 BigDecimal.ZERO 초기화
     * - quantity < 0 이면(음수) quantity 0 초기화
     */
    @Builder(access = AccessLevel.PROTECTED)
    protected Item(Long id,String name, BigDecimal price, int quantity){
        this.id = (id == null || id <= 0)? -1 : id;
        this.name = name;
        this.price = price == null || price.compareTo(BigDecimal.ZERO) == -1 ? BigDecimal.ZERO : price;
        this.quantity = quantity < 0 ? 0 : quantity;
    }
	public static Item doMakeEmptyOne() {
		return null;
	}
}
