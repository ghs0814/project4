package com.example.project4.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

//사용자가 물건을 주문할 때 아이템 목록
@Getter
@Setter
public class OrderItemRequestDto {
    @NotNull
    @Positive
    private Long productId;

    @NotNull
    @Positive
    private Integer quantity;

}
