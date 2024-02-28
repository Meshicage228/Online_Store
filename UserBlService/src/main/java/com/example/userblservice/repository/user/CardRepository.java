package com.example.userblservice.repository.user;

import com.example.userblservice.entity.user.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<UserCard, Integer> {
}
