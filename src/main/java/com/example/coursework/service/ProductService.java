package com.example.coursework.service;

import com.example.coursework.domain.ProductSearchDto;
import com.example.coursework.dto.ProductDto;
import com.example.coursework.entity.ProductEntity;
import com.example.coursework.entity.ProductImage;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDto save(ProductDto dto, MultipartFile file) throws IOException;
    Page<ProductEntity> findAll(Integer page, Integer size, ProductSearchDto search);

    void update(Integer id, MultipartFile file);

    ProductDto getById(Integer id);

    ProductDto update(Integer id, ProductDto dto);

    void delete(Integer id);

    void deleteImage(Integer id);
}
