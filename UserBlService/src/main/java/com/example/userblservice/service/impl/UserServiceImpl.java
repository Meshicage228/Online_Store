package com.example.userblservice.service.impl;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.entity.product.Commentary;
import com.example.userblservice.entity.product.ProductEntity;
import com.example.userblservice.entity.user.UserCard;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.entity.user.UsersCart;
import com.example.userblservice.mapper.user.UserMapper;
import com.example.userblservice.repository.user.CardRepository;
import com.example.userblservice.repository.user.CartRepository;
import com.example.userblservice.repository.user.CommentaryRepository;
import com.example.userblservice.repository.product.ProductRepository;
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
    UserMapper userMapper;
    ProductRepository productRepository;
    CommentaryRepository commentaryRepository;
    CardRepository cardRepository;
    CartRepository cartRepository;
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

    @Override
    public boolean addToCart(UUID userId, Integer prodId, Integer count) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException());
        ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new RuntimeException());

        cartRepository.save(UsersCart.builder()
                        .countToBuy(count)
                        .product(productEntity)
                        .user(userEntity)
                .build());

        return true;
    }

    @Override
    public boolean addToFavorite(UUID userId, Integer prodId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException());
        ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new RuntimeException());

        return userEntity.AddToFavorite(productEntity);
    }

    @Override
    public Commentary addComment(UUID userId, Integer prodId, String comment) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException());
        ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new RuntimeException());

        Commentary build = Commentary.builder()
                .user(userEntity)
                .product(productEntity)
                .comment(comment)
                .build();

        return commentaryRepository.save(build);
    }

    @Override
    public void addCard(UUID userId) {
        userRepository.findById(userId).ifPresentOrElse(user -> {
                   cardRepository.save(UserCard.builder()
                            .money(99999f)
                            .user(user)
                            .build());
                }, () -> new RuntimeException());
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
