package com.springboot.hibernate.miri.springboothibernatemiri.market;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.log4j.Log4j2;

// @Transactional
// @Rollback
@SpringBootTest
@Log4j2
public class marketTests {
    @Test
    public void init(){
        log.info("hello world!");
    }

    @Test
    public void repositoryTest(){
        // sprin data jpa
        
        // 1. item 초기화

        // 2. orderItem, Order 초기화 후 save
        
        // Item item = Item.builder()
        //             .
    }
}
