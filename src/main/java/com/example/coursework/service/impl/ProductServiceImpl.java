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
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    ProductRepository repository;
    ProductMapper mapper;
    @Override
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

    @Override
    public List<ProductDto> findAll() {
        return mapper.toDtos(repository.findAll());
    }
}
