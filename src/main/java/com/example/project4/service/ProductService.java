package com.example.project4.service;

import com.example.project4.domain.Product;
import com.example.project4.dto.ProductRequestDto;
import com.example.project4.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductMapper productMapper;
    //전체조회
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productMapper.findAll();
    }
    //단건조회
    @Transactional(readOnly = true)
    public Product findById(Long productId) {
        Product product = productMapper.findById(productId);
        if(product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"상품이 존재하지 않습니다.");
        }
        return product;

    }
    //등록
    public Product create(ProductRequestDto request){
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        productMapper.insert(product);
        return findById(product.getProductId());
    }
    //수정
    public Product update(Long productId, ProductRequestDto request){
        Product product = findById(productId);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        productMapper.update(product);

        return  findById(productId);
    }
    //삭제
    public void delete(Long productId) {
        findById(productId); //확인
        productMapper.delete(productId);
    }
}
