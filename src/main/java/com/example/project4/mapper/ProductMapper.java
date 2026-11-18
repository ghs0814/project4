package com.example.project4.mapper;

import com.example.project4.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> findAll();
    Product findById(@Param("productId") Long productId);

    int insert(Product product);

    int update(Product product);

    int delete(@Param("productId") Long productId);

    int decreaseStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );

    int increaseStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );

}
