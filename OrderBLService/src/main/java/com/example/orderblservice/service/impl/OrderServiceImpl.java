package com.example.orderblservice.service.impl;

import com.example.orderblservice.domain.OrderStatus;
import com.example.orderblservice.dto.product.OrderDto;
import com.example.orderblservice.dto.product.OrderSearchDto;
import com.example.orderblservice.entity.product.Orders;
import com.example.orderblservice.entity.product.ProductEntity;
import com.example.orderblservice.entity.user.UserCard;
import com.example.orderblservice.entity.user.UserEntity;
import com.example.orderblservice.exceptions.OutOfStockException;
import com.example.orderblservice.exceptions.ProductNotFoundException;
import com.example.orderblservice.exceptions.UserNotFoundException;
import com.example.orderblservice.mapper.OrderMapper;
import com.example.orderblservice.repository.CartRepository;
import com.example.orderblservice.repository.OrderRepository;
import com.example.orderblservice.repository.ProductRepository;
import com.example.orderblservice.repository.UserRepository;
import com.example.orderblservice.service.OrderService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedRate = 5)
    @Transactional
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
    @Transactional
    public boolean acceptPurchase(final UUID userId) {
        final UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        float totalPurchase = 0;

        for (final var obj : userEntity.getCart()) {
            final ProductEntity product = obj.getProduct();
            if (product.getCount() < obj.getCountToBuy()) {
                throw new OutOfStockException("Выбрано товара больше, чем присутствует на складе");
            }
            totalPurchase += product.getPrice() * obj.getCountToBuy();
        }

        if (userEntity.getUserCard().getMoney() < totalPurchase) {
            return false;
        } else {
            createPurchase(userEntity, totalPurchase);
        }
        return true;
    }

    @Transactional
    public void createPurchase(final UserEntity user, final float totalPurchase) {
        for (final var cartElement : user.getCart()) {
            final ProductEntity productAtMoment = cartElement.getProduct();
            final Orders order = Orders.builder()
                    .status(OrderStatus.WAITING)
                    .dateOfPurchase(new Date())
                    .countOfProduct(cartElement.getCountToBuy())
                    .bill(cartElement.getCountToBuy() * productAtMoment.getPrice())
                    .priceAtMomentBuying(productAtMoment.getPrice())
                    .product(cartElement.getProduct())
                    .user(user)
                    .build();
            productAtMoment.setCount(productAtMoment.getCount() - cartElement.getCountToBuy());
            orderRepository.save(order);
            cartRepository.deleteById(cartElement.getId());
        }
        final UserCard userCard = user.getUserCard();
        userCard.setMoney(userCard.getMoney() - totalPurchase);
    }

    @Override
    @Transactional
    public Page<OrderDto> findAllWithSort(final Integer page, final Integer size,
                                          final OrderSearchDto searchDto, final String sortedBy) {
        final Specification<Orders> specification = createSpecification(searchDto);
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

    @Override
    public boolean haveBoughtProd(final UUID userId, final Integer prodId) {
        final UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        final ProductEntity product = productRepository.findById(prodId)
                .orElseThrow(() -> new ProductNotFoundException("Продкут не найден"));

        return orderRepository.existsByUserAndProduct(user, product);
    }

    private Specification<Orders> createSpecification(OrderSearchDto dto) {
        return (root, query, builder) -> {
            final String name = dto.getName();
            final OrderStatus status = dto.getStatus();
            final String title = dto.getTitle();
            final UUID userId = dto.getUser_id();

            final var predicates = new ArrayList<>();

            if (isNotBlank(name)) {
                final Join<Orders, UserEntity> user = root.join("user");
                predicates.add(builder.like(user.get("name"), "%" + name.substring(1).toLowerCase().trim() + "%"));
            }
            if (nonNull(userId)) {
                final Join<Orders, UserEntity> user = root.join("user");
                predicates.add(builder.equal(user.get("id"), userId));
            }
            if (nonNull(status)) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (isNotBlank(title)) {
                final Join<Orders, ProductEntity> product = root.join("product");
                predicates.add(builder.like(product.get("title"), "%" + title.substring(1).toLowerCase().trim() + "%"));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
