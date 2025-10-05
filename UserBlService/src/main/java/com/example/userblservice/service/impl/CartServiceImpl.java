package com.example.userblservice.service.impl;

import com.example.applicationexceptionstarter.exception.ProductNotFoundException;
import com.example.applicationexceptionstarter.exception.UserNotFoundException;
import com.example.userblservice.entity.product.ProductEntity;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.entity.user.UsersCart;
import com.example.userblservice.repository.product.ProductRepository;
import com.example.userblservice.repository.user.CartRepository;
import com.example.userblservice.repository.user.UserRepository;
import com.example.userblservice.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public void changeCount(final Integer id, final String option) {
        final UsersCart usersCart = cartRepository.findById(id).get();
        Integer currentCount = usersCart.getCountToBuy();
        switch (option){
            case "increment" -> usersCart.setCountToBuy(++currentCount);
            case "decrement" -> {
                if(currentCount - 1 > 0){
                    usersCart.setCountToBuy(--currentCount);
                }
            }
        }
    }
    @Override
    public void addToCart(final UUID userId, final Integer prodId) {
        final UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        final ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));
        if(!cartRepository.existsByUserAndProduct(userEntity, productEntity)) {
            cartRepository.save(
                    UsersCart.builder()
                            .countToBuy(1)
                            .product(productEntity)
                            .user(userEntity)
                            .build());
        }
    }
    @Override
    public void deleteFromCart(final Integer cartId) {
        cartRepository.deleteById(cartId);
    }
}
