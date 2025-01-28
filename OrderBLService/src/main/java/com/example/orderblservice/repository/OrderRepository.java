package com.example.orderblservice.repository;

import com.example.orderblservice.entity.product.Orders;
import com.example.orderblservice.entity.product.ProductEntity;
import com.example.orderblservice.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer>,
                                         JpaSpecificationExecutor<Orders>,
                                         PagingAndSortingRepository<Orders, Integer> {
    boolean existsByUserAndProduct (UserEntity entity, ProductEntity product);
}
