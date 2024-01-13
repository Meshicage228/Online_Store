package com.example.coursework.service.impl;

import com.example.coursework.domain.ProductSearchDto;
import com.example.coursework.dto.ProductDto;
import com.example.coursework.entity.ProductEntity;
import com.example.coursework.mappers.ProductMapper;
import com.example.coursework.repository.ImageRepository;
import com.example.coursework.repository.ProductRepository;
import com.example.coursework.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ProductDto save(ProductDto dto, MultipartFile file) {
        ProductEntity entity = mapper.toEntity(dto);
        try {
            entity.addImage(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ProductEntity save = productRepository.save(entity);

        return mapper.toDto(save);
    }

    @Override
    public Page<ProductEntity> findAll(Integer page, Integer size, ProductSearchDto search) {
        Specification<ProductEntity> specification = createSpecification(search);
        return productRepository.findAll(specification, PageRequest.of(page - 1, size));
    }

    @Override
    public void update(Integer idImage, MultipartFile file) {
        imageRepository.findById(idImage).ifPresentOrElse(image -> {
            try {
                image.setImage(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> new RuntimeException());
    }

    @Override
    public ProductDto update(Integer id, ProductDto dto) {
        ProductEntity productEntity = productRepository.findById(id).orElseThrow(() -> new RuntimeException());
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
    public ProductDto getById(Integer id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException());

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

