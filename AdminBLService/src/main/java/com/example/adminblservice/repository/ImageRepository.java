package com.example.adminblservice.repository;

import com.example.adminblservice.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ProductImage, Integer> {
    // TODO: 13.01.2024 Странно, почему не работет удаление по айди от хибера
    @Modifying
    @Query("delete from ProductImage where id = :id")
    void deleteByHand(Integer id);
}
