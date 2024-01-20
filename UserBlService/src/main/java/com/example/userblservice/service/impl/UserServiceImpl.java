package com.example.userblservice.service.impl;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.mapper.product.ProductMapper;
import com.example.userblservice.mapper.user.UserMapper;
import com.example.userblservice.repository.user.UserRepository;
import com.example.userblservice.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    ProductMapper productMapper;
    UserMapper userMapper;
    @Override
    public UserDto save(UserDto dto) {
        UserEntity entity = userMapper.toEntity(dto);
        UserEntity save = userRepository.save(entity);
        return userMapper.toDto(save);
    }
}
