package com.example.orderblservice.repository;

import com.example.orderblservice.domain.OrderStatus;
import com.example.orderblservice.entity.product.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Integer>,
                                         JpaSpecificationExecutor<Orders>,
                                         PagingAndSortingRepository<Orders, Integer> {
    List<Orders> getOrdersByStatus(OrderStatus status);
}
