package com.springboot.hibernate.miri.springboothibernatemiri.repository;

import com.springboot.hibernate.miri.springboothibernatemiri.market.Item;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item,Long> {
    
}
