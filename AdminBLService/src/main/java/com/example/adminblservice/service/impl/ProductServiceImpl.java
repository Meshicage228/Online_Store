package com.example.adminblservice.service.impl;


import com.example.adminblservice.dto.ProductDto;
import com.example.adminblservice.dto.ProductSearchDto;
import com.example.adminblservice.entity.product.ProductEntity;
import com.example.adminblservice.exceptions.IdNotFountException;
import com.example.adminblservice.mappers.ProductMapper;
import com.example.adminblservice.repository.ImageRepository;
import com.example.adminblservice.repository.ProductRepository;
import com.example.adminblservice.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    ProductMapper mapper;
    ImageRepository imageRepository;
    @Override
    public ProductDto save(ProductDto dto) {
        ProductEntity entity = mapper.toEntity(dto);
        try {
            for(var file: dto.getFileImage()){
                entity.addImage(file.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ProductEntity save = productRepository.save(entity);

        return mapper.toDto(save);
    }

    @Override
    public Page<ProductDto> findAll(Integer page, Integer size, ProductSearchDto search) {
        Specification<ProductEntity> specification = createSpecification(search);
        return productRepository.findAll(specification, PageRequest.of(page - 1, size))
                .map(mapper::toDto);
    }

    @Override
    public void update(Integer idImage, MultipartFile file) {
        imageRepository.findById(idImage).ifPresentOrElse(image -> {
            try {
                image.setImage(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> new IdNotFountException("wrong id"));
    }

    @Override
    public ProductDto update(Integer id, ProductDto dto) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new IdNotFountException("wrong id"));

        ProductEntity updated = mapper.update(productEntity, mapper.toEntity(dto));
        return mapper.toDto(updated);
    }

    @Override
    public void delete(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public void deleteImage(Integer id) {
        imageRepository.deleteByHand(id);
    }

    @Override
    public Page<ProductDto> getAllProducts(Integer page, Integer size, String sortedBy, ProductSearchDto searchDto){
        Specification<ProductEntity> specification = createSpecification(searchDto);
        switch (sortedBy){
            case "priceUp" -> {
                return productRepository.findAll(specification, PageRequest.of(page, size)
                                        .withSort(Sort.by(Sort.Direction.ASC, "price")))
                        .map(mapper::toDto);
            }
            case "priceDown" -> {
                return productRepository.findAll(specification, PageRequest.of(page, size)
                                        .withSort(Sort.by(Sort.Direction.DESC, "price")))
                        .map(mapper::toDto);
            }
            case "defaultOrder" -> {
                return productRepository.findAll(specification, PageRequest.of(page, size))
                        .map(mapper::toDto);
            }
            default -> {
                return productRepository.findAll(specification, PageRequest.of(page, size)
                                        .withSort(Sort.by(Sort.Direction.ASC, sortedBy)))
                        .map(mapper::toDto);
            }
        }
    }

    @Override
    public ProductDto findByIdProduct(Integer id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new IdNotFountException("wrong id"));

        return mapper.toDto(productEntity);
    }

    private Specification<ProductEntity> createSpecification(ProductSearchDto dto) {
        return (root, query, builder) -> {
            String title = dto.getTitle();
            Float price = dto.getPrice();
            var predicates = new ArrayList<>();

            if (isNotBlank(title) && nonNull(title)) {
                title.toLowerCase().trim();
                predicates.add(builder.like(root.get("title"), "%" + title + "%"));
            }
            if (nonNull(price)) {
                predicates.add(builder.le(root.get("price"), price));
            }

            Predicate[] array = predicates.toArray(Predicate[]::new);

            return builder.and(array);
        };
    }
}

