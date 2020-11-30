package com.springboot.hibernate.miri.springboothibernatemiri.repository;

import com.springboot.hibernate.miri.springboothibernatemiri.market.Order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
    
}
