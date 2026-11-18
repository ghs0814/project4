package com.example.project4.controller;


import com.example.project4.domain.Product;
import com.example.project4.dto.ProductRequestDto;
import com.example.project4.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    
    //전체 조회
    @GetMapping
    public List<Product> findAll() {
        return productService.findAll();
    }
    //단건 조회
    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id) {
        return productService.findById(id);
    }
    //등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(
            @Valid @RequestBody ProductRequestDto request){
        return productService.create(request);

    }

    //수정
    @PutMapping("/{id}")
    public Product update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto request
    ){
        return productService.update(id, request);
    }

    //삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        productService.delete(id);
    }
}
