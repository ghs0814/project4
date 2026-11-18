package com.example.project4.mapper;


import com.example.project4.domain.Order;
import com.example.project4.domain.OrderItem;

import com.example.project4.dto.OrderSearchConditionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    //주문 테이블에 주문 저장
    int insert(Order order);

    //주문 상품 테이블에 저장
    int insertItem(OrderItem orderItem);

    Order findById(@Param("orderId") Long orderId);

    List<Order> findAll(OrderSearchConditionDto condition); //객체로 받는 경우, 핃드를 바로 #{}로 사용가능함.

    List<OrderItem> findItemsByOrderId(@Param("orderId") Long orderId);

    int cancel(@Param("orderId") Long orderId);

    long count(OrderSearchConditionDto condition);

}
