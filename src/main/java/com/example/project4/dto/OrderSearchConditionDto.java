package com.example.project4.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchConditionDto {

    // 검색 조건 (선택)
    @Positive
    private Long customerId;

    @Pattern(regexp = "ORDERED|CANCELED")
    private String status;

    // 정렬
    @NotBlank
    @Pattern(regexp = "newest|oldest")
    private String sort = "newest";

    // 페이지 번호 (1부터 시작)
    @Min(1)
    private int page = 1;

    // 한 페이지당 조회 건수
    @Min(1)
    @Max(100)
    private int size = 5;

    // SQL OFFSET 계산
    public long getOffset() {
        return (long) (page - 1) * size;
    }
    //mybatis의 #{} 문법은 getter와 동일, #{offset} = getOffset().

    /*
    * 1. 검색 -> 검색 조건 dto
    * 2. page&size + offset((page-1) * size)
    * 3. sort
    * 4. conditions
    *
    *
    * @Pattern(regexp = "ex1 | ex2")
    *
    * */

}