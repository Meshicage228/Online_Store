package com.example.adminblservice.repository;

import com.example.adminblservice.entity.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ProductImage, Integer> {
    @Modifying
    @Query("delete from ProductImage where id = :id")
    void deleteByHand(Integer id);
}
