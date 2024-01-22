package com.example.userblservice.service.impl;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.mapper.product.ProductMapper;
import com.example.userblservice.mapper.user.UserMapper;
import com.example.userblservice.repository.user.UserRepository;
import com.example.userblservice.service.UserService;
import com.example.userblservice.dto.user.UserSearchDto;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Service
@Transactional
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

    @Override
    public Page<UserDto> findAll(Integer page, Integer size, UserSearchDto dto) {
        Specification<UserEntity> specification = createSpecification(dto);

        return userRepository.findAll(specification, PageRequest.of(page, size))
                .map(userMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto getByid(UUID id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new RuntimeException());

        return userMapper.toDto(userEntity);
    }

    @Override
    public UserDto update(UUID id, UserDto dto) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException());
        UserEntity updated = userMapper.update(userEntity, userMapper.toEntity(dto));

        return userMapper.toDto(updated);
    }

    private Specification<UserEntity> createSpecification(UserSearchDto dto) {
        return (root, query, builder) -> {
            String name = dto.getName();
            var predicates = new ArrayList<>();

            if (isNotBlank(name) && nonNull(name)) {
                name.toLowerCase().trim();
                predicates.add(builder.like(root.get("name"), "%" + name + "%"));
            }

            Predicate[] array = predicates.toArray(Predicate[]::new);

            return builder.and(array);
        };
    }
}
