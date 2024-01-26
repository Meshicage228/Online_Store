package com.example.orderblservice.repository;


import com.example.orderblservice.entity.user.UsersCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<UsersCart, Integer> {
}
