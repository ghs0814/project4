package com.example.project4.controller;

import com.example.project4.domain.Customer;
import com.example.project4.dto.CustomerRequestDto;
import com.example.project4.service.CustomerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

   private  final CustomerService customerService;

   //전체 조회
    @GetMapping
    public List<Customer> findAll(){
        return customerService.findAll();
    }
    //단건 조회
    @GetMapping("/{id}")
    public Customer findById(
            @PathVariable Long id
    ){
        return customerService.findById(id);
    }

    //등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(
            @Valid @RequestBody CustomerRequestDto request
    ){
        return customerService.create(request);
    }

    //수정
    @PutMapping("/{id}")
    public Customer update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto request
    ){
      return  customerService.update(id, request);
    }

    //삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ){
        customerService.delete(id);
    }
}