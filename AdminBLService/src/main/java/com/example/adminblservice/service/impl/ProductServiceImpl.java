package com.example.adminblservice.service.impl;

import com.example.adminblservice.dto.product.ProductDto;
import com.example.adminblservice.dto.product.ProductSearchDto;
import com.example.adminblservice.entity.product.ProductEntity;
import com.example.adminblservice.entity.user.Commentary;
import com.example.adminblservice.exceptions.ProductNotFoundException;
import com.example.adminblservice.mappers.product.ProductMapper;
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
import java.util.*;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    ProductMapper mapper;
    ImageRepository imageRepository;

    @Override
    @Transactional
    public ProductDto save(ProductDto dto) {
        ProductEntity entity = mapper.toEntity(dto);
        dto.getImagesToThrow().forEach(entity::addImage);
        ProductEntity save = productRepository.save(entity);

        return mapper.toDto(save);
    }

    @Override
    @Transactional
    public void update(Integer idImage, MultipartFile file) {
        imageRepository.findById(idImage).ifPresentOrElse(image -> {
            try {
                image.setImage(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> new RuntimeException("wrong id"));
    }

    @Override
    @Transactional
    public ProductDto update(Integer id, ProductDto dto) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("wrong id"));

        ProductEntity updated = mapper.update(productEntity, mapper.toEntity(dto));
        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteImage(Integer id) {
        imageRepository.deleteByHand(id);
    }

    @Override
    @Transactional
    public void addImage(Integer id, MultipartFile file) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));
        try {
            entity.addImage(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
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
    @Transactional
    public ProductDto findByIdProduct(Integer id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));

        if(!productEntity.getComments().isEmpty()){
            productEntity.getComments().sort(Comparator.comparing(Commentary::getDate).reversed());
        }

        return mapper.toDto(productEntity);
    }

    private Specification<ProductEntity> createSpecification(ProductSearchDto dto) {
        return (root, query, builder) -> {
            String title = dto.getTitle();
            Float price = dto.getPrice();
            var predicates = new ArrayList<>();

            if (isNotBlank(title) && nonNull(title)) {
                predicates.add(builder.like(root.get("title"), "%" + title.substring(1).toLowerCase().trim() + "%"));
            }
            if (nonNull(price)) {
                predicates.add(builder.le(root.get("price"), price));
            }

            Predicate[] array = predicates.toArray(Predicate[]::new);

            return builder.and(array);
        };
    }
}

