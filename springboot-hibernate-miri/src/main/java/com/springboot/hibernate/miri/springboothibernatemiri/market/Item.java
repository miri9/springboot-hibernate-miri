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
 */

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tbl_item")
@AllArgsConstructor
@NoArgsConstructor
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
    
    
    @Builder
    public Item(String name, BigDecimal price, int quantity){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
