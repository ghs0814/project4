package com.example.project4.dto;

import com.example.project4.domain.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderPageResponseDto {

    private List<Order> content;

    private int page;

    private int size;

    private long totalElements;

    private long totalPages;


    /*
    * #페이징
    * 1. List<obj> content
    * 2. 현재page & 현재size
    * 3. totalElements&totalPages
    *
    *
    *
    * */
}