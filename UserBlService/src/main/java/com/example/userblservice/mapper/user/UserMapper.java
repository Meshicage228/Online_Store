package com.example.userblservice.mapper.user;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.entity.product.ProductEntity;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.mapper.product.ProductImageMapper;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;


@Mapper(
        componentModel = "spring",
        uses = {ProductEntity.class, ProductImageMapper.class}
)
public interface UserMapper {

    // TODO: 20.01.2024 Encode and decode password!
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "avatar", expression = "java(decodeStringToBytes(entity.getAvatar()))"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "role", source = "role"),
            @Mapping(target = "favoriteProducts", source = "favoriteProducts"),
            @Mapping(target = "basket", source = "cart")
    })
    UserDto toDto(UserEntity entity);
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "avatar", expression = "java(encodeBytesToString(dto.getFile()))"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "role", defaultValue = "USER")
    })
    UserEntity toEntity(UserDto dto);


     List<UserDto> toDtos(List<UserEntity> dtos);

    // TODO: 21.01.2024 Resolve update avatar and password
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "password", source = "password")
    UserEntity update(@MappingTarget UserEntity target, UserEntity source);

    default byte[] encodeBytesToString(MultipartFile file){
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    default String decodeStringToBytes(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

}
