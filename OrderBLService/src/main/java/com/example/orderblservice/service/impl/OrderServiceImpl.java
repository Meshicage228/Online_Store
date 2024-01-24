package com.example.orderblservice.service.impl;

import com.example.orderblservice.domain.OrderStatus;
import com.example.orderblservice.dto.product.OrderDto;
import com.example.orderblservice.dto.product.OrderRequest;
import com.example.orderblservice.entity.product.Orders;
import com.example.orderblservice.entity.product.ProductEntity;
import com.example.orderblservice.entity.user.UserEntity;
import com.example.orderblservice.mapper.OrderMapper;
import com.example.orderblservice.repository.OrderRepository;
import com.example.orderblservice.repository.ProductRepository;
import com.example.orderblservice.repository.UserRepository;
import com.example.orderblservice.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Date;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    OrderRepository repository;
    ProductRepository productRepository;
    UserRepository userRepository;
    OrderMapper orderMapper;

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
                .priceAtMomentBuying(productEntity.getPrice())
                .product(productEntity)
                .user(userEntity)
                .build();

        Orders saved = repository.save(order);

        return orderMapper.toDto(saved);
    }
}
