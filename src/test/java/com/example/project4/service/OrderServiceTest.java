package com.example.project4.service;

import com.example.project4.domain.Customer;
import com.example.project4.domain.Order;
import com.example.project4.domain.OrderItem;
import com.example.project4.domain.Product;
import com.example.project4.dto.OrderCreateRequestDto;
import com.example.project4.dto.OrderItemRequestDto;
import com.example.project4.mapper.CustomerMapper;
import com.example.project4.mapper.OrderMapper;
import com.example.project4.mapper.ProductMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createShouldDecreaseStock() {

        // given
        when(customerMapper.findById(1L))
                .thenReturn(new Customer());

        Product product = new Product();
        product.setProductId(2L);
        product.setPrice(new BigDecimal("35000"));

        when(productMapper.findById(2L))
                .thenReturn(product);

        when(productMapper.decreaseStock(2L, 2))
                .thenReturn(1);

        when(orderMapper.insert(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setOrderId(10L);
                    return 1;
                }); // order 객체의 getOrderId()를 10으로 세팅해주기 위해서 invocation 사용
        //그냥 thenReturn(1)로하면, orderId는 null인 상태
        //thenAnswer : 단순 값 반환을 넘어, 호출 시점에 넘어온 인자(invocation)를 직접 꺼내서 상태를 조작(Side Effect 재현)한 뒤 결과를 반환

        Order savedOrder = new Order();
        savedOrder.setOrderId(10L);
        savedOrder.setStatus("ORDERED");

        when(orderMapper.findById(10L))
                .thenReturn(savedOrder);

        when(orderMapper.findItemsByOrderId(10L))
                .thenReturn(List.of());

        OrderCreateRequestDto request = createRequest(1L, 2L, 2);

        // when
        Order result = orderService.create(request);

        // then
        assertEquals(10L, result.getOrderId());

        verify(productMapper).decreaseStock(2L, 2);
        verify(orderMapper).insertItem(any(OrderItem.class));

        ArgumentCaptor<Order> captor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderMapper).insert(captor.capture()); // 1. 호출 확인 + 캡처

        assertEquals(
                0,
                new BigDecimal("70000")
                        .compareTo(captor.getValue().getTotalAmount())
        ); // 2. 캡처한거 검증, compareTo는 같으면 -1. 0, 1 반환
    }

    @Test
    void createShouldFailWhenStockIsInsufficient() {

        // given
        when(customerMapper.findById(1L))
                .thenReturn(new Customer());

        Product product = new Product();
        product.setProductId(2L);
        product.setPrice(new BigDecimal("35000"));

        when(productMapper.findById(2L))
                .thenReturn(product);

        when(orderMapper.insert(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setOrderId(10L);
                    return 1;
                });

        when(productMapper.decreaseStock(2L, 100))
                .thenReturn(0);

        OrderCreateRequestDto request = createRequest(1L, 2L, 100);

        // when
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.create(request)
        );

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(productMapper).decreaseStock(2L, 100);
    }

    @Test
    void cancelShouldRestoreStock() {

        // given
        Order ordered = new Order();
        ordered.setOrderId(10L);
        ordered.setStatus("ORDERED");

        Order canceled = new Order();
        canceled.setOrderId(10L);
        canceled.setStatus("CANCELED");

        OrderItem item = new OrderItem();
        item.setProductId(2L);
        item.setQuantity(2);

        when(orderMapper.findById(10L))
                .thenReturn(ordered, canceled);

        when(orderMapper.findItemsByOrderId(10L))
                .thenReturn(List.of(item));
        // List.of() -> 불변 리스트 -> sort()를 허용하지 않음 -> 오류 발생

        when(orderMapper.cancel(10L))
                .thenReturn(1);

        when(productMapper.increaseStock(2L, 2))
                .thenReturn(1);

        // when
        Order result = orderService.cancel(10L);

        // then
        assertEquals("CANCELED", result.getStatus());

        verify(orderMapper).cancel(10L);
        verify(productMapper).increaseStock(2L, 2);
    }

    @Test
    void cancelShouldFailWhenAlreadyCanceled() {

        // given
        Order order = new Order();
        order.setOrderId(10L);
        order.setStatus("CANCELED");

        when(orderMapper.findById(10L))
                .thenReturn(order);

        when(orderMapper.findItemsByOrderId(10L))
                .thenReturn(List.of());

        // when
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> orderService.cancel(10L)
        );

        // then
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(orderMapper, never()).cancel(any(Long.class));
        verify(productMapper, never())
                .increaseStock(any(Long.class), any(int.class));
    }

    // 테스트용 주문 요청 생성
    private OrderCreateRequestDto createRequest(
            Long customerId,
            Long productId,
            int quantity
    ) {

        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setProductId(productId);
        item.setQuantity(quantity);

        OrderCreateRequestDto request = new OrderCreateRequestDto();
        request.setCustomerId(customerId);
        request.setItems(List.of(item));

        return request;
    }
}