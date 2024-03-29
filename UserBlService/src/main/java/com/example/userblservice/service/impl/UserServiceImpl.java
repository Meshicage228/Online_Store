package com.example.userblservice.service.impl;

import com.example.userblservice.dto.product.ProductDto;
import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.entity.product.Commentary;
import com.example.userblservice.entity.product.ProductEntity;
import com.example.userblservice.entity.user.UserCard;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.exceptions.ProductNotFoundException;
import com.example.userblservice.exceptions.UserNotFoundException;
import com.example.userblservice.mapper.user.UserMapper;
import com.example.userblservice.repository.user.CardRepository;
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

import java.util.*;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    ProductRepository productRepository;
    CommentaryRepository commentaryRepository;
    CardRepository cardRepository;

    @Override
    @Transactional
    public UserDto save(UserDto dto) {
        UserEntity entity = userMapper.toEntity(dto);
        UserEntity save = userRepository.save(entity);
        return userMapper.toDto(save);
    }

    @Override
    @Transactional
    public Page<UserDto> findAll(Integer page, Integer size, UserSearchDto dto) {
        Specification<UserEntity> specification = createSpecification(dto);

        return userRepository.findAll(specification, PageRequest.of(page, size))
                .map(userMapper::toDto);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto getByid(UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        UserDto dto = userMapper.toDto(userEntity);
        dto.getBasket().sort(Comparator.comparing(ProductDto::getTitle));
        return dto;
    }

    @Override
    @Transactional
    public boolean addToFavorite(UUID userId, Integer prodId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найдет"));

        return userEntity.AddToFavorite(productEntity);
    }

    @Override
    @Transactional
    public Commentary addComment(UUID userId, Integer prodId, String comment) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));

        Commentary build = Commentary.builder()
                .user(userEntity)
                .date(new Date())
                .product(productEntity)
                .comment(comment)
                .build();

        return commentaryRepository.save(build);
    }

    @Override
    @Transactional
    public UserCard addCard(UUID userId) {
        Optional<UserEntity> byId = userRepository.findById(userId);
        if (byId.isPresent()) {
            UserCard save = cardRepository.save(UserCard.builder()
                    .money(99999f)
                    .user(byId.get())
                    .build());
            return save;
        }
        if (byId.isEmpty()) {
            throw new UserNotFoundException("Не найдено пользователя");
        }
        return null;
    }

    @Override
    @Transactional
    public boolean findByName(String name) {
        return userRepository.existsByName(name);
    }

    @Override
    @Transactional
    public boolean checkExists(String authName, String password) {
        String encoded = userMapper.getEncoder().encode(password);
        return userRepository.existsByNameAndPassword(authName, encoded);
    }

    @Override
    public void deleteCommentary(Integer id) {
        commentaryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void removeFavorite(UUID userId, Integer prodId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найдет"));

        userEntity.removeFavorite(productEntity);
    }


    private Specification<UserEntity> createSpecification(UserSearchDto dto) {
        return (root, query, builder) -> {
            String name = dto.getName();
            var predicates = new ArrayList<>();

            if (isNotBlank(name) && nonNull(name)) {
                name.toLowerCase().trim();
                predicates.add(builder.like(root.get("name"), "%" + name.substring(1).toLowerCase().trim() + "%"));
            }

            Predicate[] array = predicates.toArray(Predicate[]::new);

            return builder.and(array);
        };
    }
}
