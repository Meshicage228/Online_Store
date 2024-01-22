package com.example.userblservice.service;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.dto.user.UserSearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {
    UserDto save(UserDto dto);

    Page<UserDto> findAll(Integer page, Integer size, UserSearchDto dto);

    void delete(UUID id);

    UserDto getByid(UUID id);

    UserDto update(UUID id, UserDto dto);
}
