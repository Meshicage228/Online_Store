package com.example.orderblservice.repository;

import com.example.orderblservice.entity.user.UsersCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<UsersCart, Integer> {
}
