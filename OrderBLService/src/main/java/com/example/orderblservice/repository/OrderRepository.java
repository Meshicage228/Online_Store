package com.example.orderblservice.repository;

import com.example.orderblservice.entity.product.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends JpaRepository<Orders, Integer>,
                                         JpaSpecificationExecutor<Orders>,
                                         PagingAndSortingRepository<Orders, Integer> {
}
