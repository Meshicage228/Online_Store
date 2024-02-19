package com.example.userblservice.service;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.dto.user.UserSearchDto;
import com.example.userblservice.entity.product.Commentary;
import com.example.userblservice.entity.user.UserCard;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {
    UserDto save(UserDto dto);

    Page<UserDto> findAll(Integer page, Integer size, UserSearchDto dto);

    void delete(UUID id);

    UserDto getByid(UUID id);

    UserDto update(UUID id, UserDto dto);

    boolean addToFavorite(UUID userId, Integer prodId);

    Commentary addComment(UUID userId, Integer prodId, String comment);

    UserCard addCard(UUID userId);

    boolean findByName(String name);

    boolean checkExists(String authName, String password);

    void deleteCommentary(Integer id);

    void removeFavorite(UUID userId, Integer prodId);
}
