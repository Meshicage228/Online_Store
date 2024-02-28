package com.example.userblservice.repository.user;

import com.example.userblservice.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID>,
                                        JpaSpecificationExecutor<UserEntity>,
                                        PagingAndSortingRepository<UserEntity, UUID> {
    Boolean existsByName(String name);

    Boolean existsByNameAndPassword(String name, String password);
}
