package com.example.coursework.service;

import com.example.coursework.dto.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductDto save(ProductDto dto, MultipartFile file) throws IOException;
    List<ProductDto> findAll();
}
