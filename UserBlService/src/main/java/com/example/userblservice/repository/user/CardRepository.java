package com.example.userblservice.repository.user;

import com.example.userblservice.entity.user.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface CardRepository extends JpaRepository<UserCard, Integer> {
}
