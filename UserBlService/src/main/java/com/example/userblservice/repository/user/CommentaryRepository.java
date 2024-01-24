package com.example.userblservice.repository.user;

import com.example.userblservice.entity.product.Commentary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentaryRepository extends JpaRepository<Commentary,Integer> {
}
