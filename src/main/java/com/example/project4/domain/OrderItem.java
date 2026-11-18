package com.example.project4.domain;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItem {
    //BIGINT
    private Long orderItemId;

    private Long orderId;

    private Long productId;

    private String productName;

    //interger 사용 이유 -> null값을 표현하기 위해, int는 null값을 가질 수 없음
    //수량이 0이면, 사용자가 입력한 0인지, 프론트에서 값을 안보낸건지 구분하기 위해서
    // validation이 제대로 동작하려면 int가 아닌 integer 사용
    //그냥 실무 표준은 integer
    //INTEGER
    private Integer quantity;

    private BigDecimal unitPrice;
}
