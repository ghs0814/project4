package com.example.project4.service;

import com.example.project4.domain.Order;
import com.example.project4.domain.OrderItem;
import com.example.project4.domain.Product;
import com.example.project4.dto.OrderCreateRequestDto;
import com.example.project4.dto.OrderItemRequestDto;
import com.example.project4.dto.OrderPageResponseDto;
import com.example.project4.dto.OrderSearchConditionDto;
import com.example.project4.mapper.CustomerMapper;
import com.example.project4.mapper.OrderMapper;
import com.example.project4.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;

    // 주문 검색 및 페이징
    public OrderPageResponseDto findAll(
            OrderSearchConditionDto condition
    ) {

        List<Order> orders = orderMapper.findAll(condition);

        long totalElements = orderMapper.count(condition);

        long totalPages = (
                totalElements + condition.getSize() - 1
        ) / condition.getSize();

        return new OrderPageResponseDto(
                orders,
                condition.getPage(),
                condition.getSize(),
                totalElements,
                totalPages
        );
    }

    // 주문 상세 조회
    public Order findById(Long orderId) {

        Order order = orderMapper.findById(orderId);

        if (order == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "주문이 존재하지 않습니다."
            );
        }

        List<OrderItem> items =
                orderMapper.findItemsByOrderId(orderId);

        order.setItems(items);

        return order;
    }

    // 주문 생성 및 재고 차감
    @Transactional
    public Order create(OrderCreateRequestDto request) {

        // 1. 고객 확인
        if (customerMapper.findById(request.getCustomerId()) == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "고객이 존재하지 않습니다."
            );
        }

        // 2. 주문 상품 준비
        List<OrderItem> items = new ArrayList<>();
        Set<Long> productIds = new HashSet<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDto requestItem : request.getItems()) {

            Long productId = requestItem.getProductId();

            if (!productIds.add(productId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "동일한 상품을 중복해서 주문할 수 없습니다."
                );
            }

            Product product = productMapper.findById(productId);

            if (product == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "상품이 존재하지 않습니다."
                );
            }

            OrderItem item = new OrderItem();

            item.setProductId(productId);
            item.setQuantity(requestItem.getQuantity());
            item.setUnitPrice(product.getPrice());

            items.add(item);

            BigDecimal itemAmount = product.getPrice()
                    .multiply(BigDecimal.valueOf(requestItem.getQuantity()));

            totalAmount = totalAmount.add(itemAmount);
        }

        // 신규: 상품 ID 오름차순 정렬
        items.sort(Comparator.comparing(OrderItem::getProductId));

        // 3. 주문 기본 정보 저장
        Order order = new Order();

        order.setCustomerId(request.getCustomerId());
        order.setTotalAmount(totalAmount);
        order.setStatus("ORDERED");

        orderMapper.insert(order);

        // 4. 주문 상품 저장 및 재고 차감
        for (OrderItem item : items) {

            item.setOrderId(order.getOrderId());

            orderMapper.insertItem(item);

            int updatedRows = productMapper.decreaseStock(
                    item.getProductId(),
                    item.getQuantity()
            );

            if (updatedRows == 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "상품 재고가 부족합니다."
                );
            }
        }

        return findById(order.getOrderId());
    }

    // 주문 취소 및 재고 복구
    @Transactional
    public Order cancel(Long orderId) {

        // 1. 주문 조회
        Order order = findById(orderId);

        if (!"ORDERED".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 취소된 주문입니다."
            );
        }

        // 2. 조건부 주문 상태 변경
        int updatedRows = orderMapper.cancel(orderId);

        if (updatedRows == 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 취소된 주문입니다."
            );
        }

        // 신규: 재고 복구도 상품 ID 순서대로 처리
//        List<OrderItem> items = order.getItems();
//
//

        // 수정 후 (new ArrayList<>로 감싸서 정렬), 불변성 제거
        List<OrderItem> items = new ArrayList<>(order.getItems());
        items.sort(Comparator.comparing(OrderItem::getProductId));

        // 3. 상품 재고 복구
        for (OrderItem item : items) {

            int restoredRows = productMapper.increaseStock(
                    item.getProductId(),
                    item.getQuantity()
            );

            if (restoredRows == 0) {
                throw new IllegalStateException(
                        "재고 복구 실패: 상품이 존재하지 않습니다."
                );
            }
        }

        return findById(orderId);
    }
}