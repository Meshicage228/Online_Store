package com.example.orderblservice.service.impl;

import com.example.orderblservice.domain.OrderStatus;
import com.example.orderblservice.dto.product.OrderDto;
import com.example.orderblservice.dto.product.OrderRequest;
import com.example.orderblservice.dto.product.OrderSearchDto;
import com.example.orderblservice.entity.product.Orders;
import com.example.orderblservice.entity.product.ProductEntity;
import com.example.orderblservice.entity.user.UserEntity;
import com.example.orderblservice.mapper.OrderMapper;
import com.example.orderblservice.repository.OrderRepository;
import com.example.orderblservice.repository.ProductRepository;
import com.example.orderblservice.repository.UserRepository;
import com.example.orderblservice.service.OrderService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    OrderMapper orderMapper;

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 5)
    public void updateStatusOrders() {
        Optional.of(orderRepository.findAll())
                .ifPresent(list -> list
                        .forEach(order -> {
                            switch (order.getStatus()) {
                                case IN_PROGRESS -> order.setStatus(OrderStatus.DONE);
                                case WAITING -> order.setStatus(OrderStatus.IN_PROGRESS);
                            }
                        }));
    }

    @Override
    public OrderDto createPurchase(OrderRequest request) {
        ProductEntity productEntity = productRepository.findById(request.getProd_id())
                .orElseThrow(() -> new RuntimeException());

        UserEntity userEntity = userRepository.findById(request.getUser_id())
                .orElseThrow(() -> new RuntimeException());

        Orders order = Orders.builder()
                .status(OrderStatus.WAITING)
                .dateOfPurchase(new Date())
                .countOfProduct(request.getCount())
                .bill(request.getCount() * productEntity.getPrice())
                .priceAtMomentBuying(productEntity.getPrice())
                .product(productEntity)
                .user(userEntity)
                .build();

        Orders saved = orderRepository.save(order);

        return orderMapper.toDto(saved);
    }

    @Override
    public Page<OrderDto> findAllWithSort(Integer page, Integer size, OrderSearchDto searchDto, String sortedBy) {
        Specification<Orders> specification = createSpecification(searchDto);
        switch (sortedBy) {
            case "billUp" -> {
                return orderRepository.findAll(specification, PageRequest.of(page, size)
                                .withSort(Sort.by(Sort.Direction.ASC, "bill")))
                        .map(orderMapper::toDto);
            }
            case "billDown" -> {
                return orderRepository.findAll(specification, PageRequest.of(page, size)
                                .withSort(Sort.by(Sort.Direction.DESC, "bill")))
                        .map(orderMapper::toDto);
            }
            case "default" -> {
                return orderRepository.findAll(specification, PageRequest.of(page, size))
                        .map(orderMapper::toDto);
            }
            default -> {
                return orderRepository.findAll(specification, PageRequest.of(page, size)
                                .withSort(Sort.by(Sort.Direction.ASC, sortedBy)))
                        .map(orderMapper::toDto);
            }
        }
    }

    private Specification<Orders> createSpecification(OrderSearchDto dto) {
        return (root, query, builder) -> {
            String name = dto.getName();
            OrderStatus status = dto.getStatus();
            String title = dto.getTitle();
            UUID userId = dto.getUser_id();

            var predicates = new ArrayList<>();

            if (isNotBlank(name) && nonNull(name)) {
                Join<Orders, UserEntity> user = root.join("user");
                predicates.add(builder.like(user.get("name"), "%" + name + "%"));
            }
            if (nonNull(userId)) {
                Join<Orders, UserEntity> user = root.join("user");
                predicates.add(builder.equal(user.get("id"), userId));
            }
            if (nonNull(status)) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (isNotBlank(title) && nonNull(title)) {
                Join<Orders, ProductEntity> product = root.join("product");
                predicates.add(builder.like(product.get("title"), "%" + title + "%"));
            }
            Predicate[] array = predicates.toArray(Predicate[]::new);

            return builder.and(array);
        };
    }
}
