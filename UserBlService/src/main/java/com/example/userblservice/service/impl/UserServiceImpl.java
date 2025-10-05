package com.example.userblservice.service.impl;

import com.example.userblservice.dto.product.ProductDto;
import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.dto.user.UserSearchDto;
import com.example.userblservice.entity.product.Commentary;
import com.example.userblservice.entity.product.ProductEntity;
import com.example.userblservice.entity.user.UserCard;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.exceptions.ProductNotFoundException;
import com.example.userblservice.exceptions.UserNotFoundException;
import com.example.userblservice.mapper.user.UserMapper;
import com.example.userblservice.repository.product.ProductRepository;
import com.example.userblservice.repository.user.CardRepository;
import com.example.userblservice.repository.user.CommentaryRepository;
import com.example.userblservice.repository.user.UserRepository;
import com.example.userblservice.service.UserService;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProductRepository productRepository;
    private final CommentaryRepository commentaryRepository;
    private final CardRepository cardRepository;

    @Override
    @Transactional
    public UserDto save(final UserDto dto) {
        final UserEntity entity = userMapper.toEntity(dto);
        final UserEntity save = userRepository.save(entity);
        return userMapper.toDto(save);
    }

    @Override
    @Transactional
    public Page<UserDto> findAll(final Integer page, final Integer size, final UserSearchDto dto) {
        final Specification<UserEntity> specification = createSpecification(dto);

        return userRepository.findAll(specification, PageRequest.of(page, size))
                .map(userMapper::toDto);
    }

    @Override
    @Transactional
    public void delete(final UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDto getByid(final UUID id) {
        final UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        final UserDto dto = userMapper.toDto(userEntity);
        dto.getBasket().sort(Comparator.comparing(ProductDto::getTitle));
        return dto;
    }

    @Override
    @Transactional
    public boolean addToFavorite(final UUID userId, final Integer prodId) {
        final UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        final ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найдет"));

        return userEntity.AddToFavorite(productEntity);
    }

    @Override
    @Transactional
    public Commentary addComment(final UUID userId, final Integer prodId, final String comment) {
        final UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        final ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден"));

        final Commentary build = Commentary.builder()
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
        final Optional<UserEntity> byId = userRepository.findById(userId);
        if (byId.isPresent()) {
            final UserCard save = cardRepository.save(UserCard.builder()
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
    public boolean findByName(final String name) {
        return userRepository.existsByName(name);
    }

    @Override
    @Transactional
    public boolean checkExists(final String authName, final String password) {
        final String encoded = userMapper.getEncoder().encode(password);
        return userRepository.existsByNameAndPassword(authName, encoded);
    }

    @Override
    public void deleteCommentary(final Integer id) {
        commentaryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void removeFavorite(final UUID userId, final Integer prodId) {
        final UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        final ProductEntity productEntity = productRepository.findById(prodId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найдет"));

        userEntity.removeFavorite(productEntity);
    }


    private Specification<UserEntity> createSpecification(final UserSearchDto dto) {
        return (root, query, builder) -> {
            final String name = dto.getName();
            final var predicates = new ArrayList<>();

            if (isNotBlank(name)) {
                name.toLowerCase().trim();
                predicates.add(builder.like(root.get("name"), "%" + name.substring(1).toLowerCase().trim() + "%"));
            }

            final Predicate[] array = predicates.toArray(Predicate[]::new);

            return builder.and(array);
        };
    }
}
