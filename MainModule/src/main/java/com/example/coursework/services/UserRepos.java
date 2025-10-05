package com.example.coursework.services;

import com.example.coursework.dto.user.CurrentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepos extends JpaRepository<CurrentUser, UUID> {
    Optional<CurrentUser> findByName(String name);
}
