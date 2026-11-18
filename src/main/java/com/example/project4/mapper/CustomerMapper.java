package com.example.project4.mapper;

import com.example.project4.domain.Customer;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper {

    List<Customer> findAll();

    Customer findById(@Param("customerId") Long customerId);

    Customer findByEmail(@Param("email") String email);

    int insert(Customer customer);

    int update(Customer customer);

    int delete(@Param("customerId") Long customerId);
}