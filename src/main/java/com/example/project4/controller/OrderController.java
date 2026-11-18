package com.example.project4.controller;


import com.example.project4.domain.Order;
import com.example.project4.dto.OrderCreateRequestDto;
import com.example.project4.dto.OrderPageResponseDto;
import com.example.project4.dto.OrderSearchConditionDto;
import com.example.project4.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(
            @Valid @RequestBody OrderCreateRequestDto request
    ) {
        return orderService.create(request);
    }

   /* @GetMapping
    public List<Order> findAll() {
        return orderService.findAll();
    }*/

    // 주문 검색 및 페이징
    //url의 쿼리 파라미터를 dto에 매핑(setter 필요) -> modelAttribute
    //modelAttribute : 1. dto 매핑 2. model 객체에 담기 / 여기서는 1번만

    @GetMapping
    public OrderPageResponseDto findAll(
            @Valid @ModelAttribute OrderSearchConditionDto condition
    ) {
        return orderService.findAll(condition);
    }

    @GetMapping("/{id}")
    public Order findById(@PathVariable Long id){
        return orderService.findById(id);
    }

    @PostMapping("/{id}/cancel")
    public Order cancel(@PathVariable Long id){
        return orderService.cancel(id);
    }
}