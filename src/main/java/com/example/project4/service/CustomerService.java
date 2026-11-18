package com.example.project4.service;

import com.example.project4.domain.Customer;
import com.example.project4.dto.CustomerRequestDto;
import com.example.project4.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerMapper customerMapper;
    @Transactional(readOnly = true)
    public List<Customer> findAll(){
        return customerMapper.findAll();
    }
    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        Customer customer = customerMapper.findById(id);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "고객이 존재하지 않습니다.");
        }
        return customer;


    }

    public Customer create(CustomerRequestDto request){
     Customer existing = customerMapper.findByEmail(request.getEmail());
     if(existing != null){
         throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 이메일입니다.");
     }
     Customer customer = new Customer();
     customer.setEmail(request.getEmail());
     customer.setName(request.getName());
     customerMapper.insert(customer);
     //mapper가 customer 객체에도 id 부여, created_At는 안넣어줌
     return findById(customer.getCustomerId());
    }

    public Customer update(Long customerId, CustomerRequestDto request){
        Customer customer =  findById(customerId);
        Customer existing = customerMapper.findByEmail(request.getEmail());
        if(existing != null && !existing.getCustomerId().equals(customerId)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 이메일입니다.");
        } // 이메일이 이미 존재하는데, 다른 id면 이메일 중복

        customer.setEmail(request.getEmail());
        customer.setName(request.getName());
        customerMapper.update(customer);

        return  findById(customer.getCustomerId());
    }

    public void delete(Long customerId){
        findById(customerId); // 확인
        customerMapper.delete(customerId);
    }
}
