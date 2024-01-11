package com.example.coursework.service.impl;

import com.example.coursework.dto.ProductDto;
import com.example.coursework.entity.ProductEntity;
import com.example.coursework.mappers.ProductMapper;
import com.example.coursework.repository.ProductRepository;
import com.example.coursework.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Service
public class ProductServiceImpl implements ProductService {
    ProductRepository repository;
    ProductMapper mapper;
    @Override
    @Transactional
    public ProductDto save(ProductDto dto, MultipartFile file)  {
        ProductEntity entity = mapper.toEntity(dto);
        try {
            entity.addImage(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ProductEntity save = repository.save(entity);

        return mapper.toDto(save);
    }
}
