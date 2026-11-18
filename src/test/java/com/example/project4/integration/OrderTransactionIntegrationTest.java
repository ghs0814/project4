package com.example.project4.integration;

import com.example.project4.dto.OrderCreateRequestDto;
import com.example.project4.dto.OrderItemRequestDto;
import com.example.project4.service.OrderService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderTransactionIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long customerId;
    private Long productAId;
    private Long productBId;

    @BeforeEach
    void setUp() {

        // 테스트 전용 고객 생성
        customerId = jdbcTemplate.queryForObject(
                """
                INSERT INTO customers (name, email)
                VALUES (?, ?)
                RETURNING customer_id
                """,
                Long.class,
                "테스트 고객",
                UUID.randomUUID() + "@example.com"
        );

        // 상품 A: 재고 5개
        productAId = jdbcTemplate.queryForObject(
                """
                INSERT INTO products (name, price, stock_quantity)
                VALUES (?, ?, ?)
                RETURNING product_id
                """,
                Long.class,
                "테스트 상품 A",
                new BigDecimal("1000"),
                5
        );

        // 상품 B: 재고 0개
        productBId = jdbcTemplate.queryForObject(
                """
                INSERT INTO products (name, price, stock_quantity)
                VALUES (?, ?, ?)
                RETURNING product_id
                """,
                Long.class,
                "테스트 상품 B",
                new BigDecimal("2000"),
                0
        );
    }

    @Test
    void createShouldRollbackWhenStockIsInsufficient() {

        // given
        OrderItemRequestDto itemA = new OrderItemRequestDto();
        itemA.setProductId(productAId);
        itemA.setQuantity(2);

        OrderItemRequestDto itemB = new OrderItemRequestDto();
        itemB.setProductId(productBId);
        itemB.setQuantity(1);

        OrderCreateRequestDto request = new OrderCreateRequestDto();
        request.setCustomerId(customerId);
        request.setItems(List.of(itemA, itemB));

        // when
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.create(request)
        );

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        // 상품 A의 재고가 복구되었는지 확인
        Integer stock = jdbcTemplate.queryForObject(
                "SELECT stock_quantity FROM products WHERE product_id = ?",
                Integer.class,
                productAId
        );

        assertEquals(Integer.valueOf(5), stock);

        // 주문 데이터가 롤백되었는지 확인
        Long orderCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE customer_id = ?",
                Long.class,
                customerId
        );

        assertEquals(Long.valueOf(0), orderCount);
    }

    @AfterEach
    void tearDown() {

        // 혹시 남아 있을 주문 데이터를 먼저 삭제
        jdbcTemplate.update(
                """
                DELETE FROM order_items
                WHERE order_id IN (
                    SELECT order_id
                    FROM orders
                    WHERE customer_id = ?
                )
                """,
                customerId
        );

        jdbcTemplate.update(
                "DELETE FROM orders WHERE customer_id = ?",
                customerId
        );

        // 테스트 상품과 고객 정리
        jdbcTemplate.update(
                "DELETE FROM products WHERE product_id IN (?, ?)",
                productAId,
                productBId
        );

        jdbcTemplate.update(
                "DELETE FROM customers WHERE customer_id = ?",
                customerId
        );
    }
}