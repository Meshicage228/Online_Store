package com.example.adminblservice.service;

import com.example.adminblservice.dto.product.ProductDto;
import com.example.adminblservice.dto.product.ProductSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDto save(ProductDto dto) throws IOException;
    Page<ProductDto> findAll(Integer page, Integer size, ProductSearchDto search);

    void update(Integer id, MultipartFile file);

    Page<ProductDto> getAllProducts(Integer page, Integer size, String sortedBy, ProductSearchDto searchDto);

    ProductDto findByIdProduct(Integer id);

    ProductDto update(Integer id, ProductDto dto);

    void delete(Integer id);

    void deleteImage(Integer id);

}
