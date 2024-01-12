package com.example.coursework.repository;

import com.example.coursework.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer>,
                                           JpaSpecificationExecutor<ProductEntity>,
                                           PagingAndSortingRepository<ProductEntity, Integer> {
}
