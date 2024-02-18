package com.example.userblservice.repository.user;

import com.example.userblservice.entity.product.ProductEntity;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.entity.user.UsersCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<UsersCart, Integer> {
    Boolean existsByUserAndProduct(UserEntity entity, ProductEntity productEntity);
}
