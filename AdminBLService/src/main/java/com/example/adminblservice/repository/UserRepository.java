package com.example.adminblservice.repository;

import com.example.adminblservice.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    // Базовые методы достаточно, специфичная логика в PurchaseRepository
}