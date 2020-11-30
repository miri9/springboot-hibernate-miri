package com.springboot.hibernate.miri.springboothibernatemiri.repository;

import com.springboot.hibernate.miri.springboothibernatemiri.market.OrderItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    
}
