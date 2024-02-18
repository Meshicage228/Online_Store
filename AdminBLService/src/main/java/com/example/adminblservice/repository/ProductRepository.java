package com.example.adminblservice.repository;

import com.example.adminblservice.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer>,
                                           JpaSpecificationExecutor<ProductEntity>,
                                           PagingAndSortingRepository<ProductEntity, Integer> {
}
