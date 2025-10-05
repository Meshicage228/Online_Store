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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public ProductDto save(final ProductDto dto) {
        final ProductEntity entity = mapper.toEntity(dto);
        dto.getImagesToThrow().forEach(entity::addImage);
        final ProductEntity save = productRepository.save(entity);

        return mapper.toDto(save);
    }

    @Override
    @Transactional
    public Page<ProductDto> findAll(final Integer page, final Integer size, final ProductSearchDto search) {
        final Specification<ProductEntity> specification = createSpecification(search);
        return productRepository.findAll(specification, PageRequest.of(page - 1, size))
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public void update(final Integer idImage, final MultipartFile file) {
        imageRepository.findById(idImage).ifPresentOrElse(image -> {
            try {
                image.setImage(file.getBytes());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> new RuntimeException("wrong id"));
    }

    @Override
    @Transactional
    public ProductDto update(final Integer id, final ProductDto dto) {
        final ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("wrong id"));

        final ProductEntity updated = mapper.update(productEntity, mapper.toEntity(dto));
        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(final Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteImage(final Integer id) {
        imageRepository.deleteByHand(id);
    }

    @Override
    @Transactional
    public void addImage(final Integer id, final MultipartFile file) {
        final ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));
        try {
            entity.addImage(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public Page<ProductDto> getAllProducts(final Integer page, final Integer size, final String sortedBy, final ProductSearchDto searchDto) {
        final Specification<ProductEntity> specification = createSpecification(searchDto);
        switch (sortedBy) {
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
    public ProductDto findByIdProduct(final Integer id) {
        final ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));

        if (!productEntity.getComments().isEmpty()) {
            productEntity.getComments().sort(Comparator.comparing(Commentary::getDate).reversed());
        }

        return mapper.toDto(productEntity);
    }

    private Specification<ProductEntity> createSpecification(final ProductSearchDto dto) {
        return (root, query, builder) -> {
            final String title = dto.getTitle();
            final Float price = dto.getPrice();
            final var predicates = new ArrayList<>();

            if (isNotBlank(title)) {
                predicates.add(builder.like(root.get("title"), "%" + title.substring(1).toLowerCase().trim() + "%"));
            }
            if (nonNull(price)) {
                predicates.add(builder.le(root.get("price"), price));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}

