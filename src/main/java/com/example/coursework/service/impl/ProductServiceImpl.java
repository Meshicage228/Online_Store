package com.example.coursework.service.impl;

import com.example.coursework.domain.ProductSearchDto;
import com.example.coursework.dto.ProductDto;
import com.example.coursework.entity.ProductEntity;
import com.example.coursework.mappers.ProductMapper;
import com.example.coursework.repository.ProductRepository;
import com.example.coursework.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Predicates;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.thymeleaf.util.Validate.notNull;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    ProductRepository repository;
    ProductMapper mapper;

    @Override
    public ProductDto save(ProductDto dto, MultipartFile file) {
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
    public Page<ProductEntity> findAll(Integer page, Integer size, ProductSearchDto search) {
        if (nonNull(search)) {
            Specification<ProductEntity> specification = createSpecification(search);
            return repository.findAll(specification, PageRequest.of(page - 1, size));
        }
        return repository.findAll(PageRequest.of(page - 1, size));
    }

    @Override
    public void update(ProductDto dto, MultipartFile file) {
        Optional<ProductEntity> byId = repository.findById(dto.getId());
        byId.ifPresent(product -> {
            ProductEntity entity = mapper.toEntity(dto);
            try {
                entity.addImage(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            mapper.update(product, entity);
        });
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
                predicates.add(builder.equal(root.get("price"), price));
            }

            Predicate[] array = predicates.toArray(Predicate[]::new);

            return builder.and(array);
        };
    }

}

