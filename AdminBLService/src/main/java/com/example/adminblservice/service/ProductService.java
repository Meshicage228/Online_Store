package com.example.adminblservice.service;

import com.example.adminblservice.dto.ProductDto;
import com.example.adminblservice.dto.ProductSearchDto;
import com.example.adminblservice.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDto save(ProductDto dto, MultipartFile file) throws IOException;
    Page<ProductEntity> findAll(Integer page, Integer size, ProductSearchDto search);

    void update(Integer id, MultipartFile file);

    ProductDto findByIdProduct(Integer id);

    ProductDto update(Integer id, ProductDto dto);

    void delete(Integer id);

    void deleteImage(Integer id);
}
