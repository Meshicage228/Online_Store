package com.example.adminblservice.mappers.user;

import com.example.adminblservice.dto.user.UserDto;
import com.example.adminblservice.entity.user.UserEntity;
import com.example.adminblservice.mappers.product.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Mapper(
        componentModel = "spring",
        uses = ProductMapper.class
)
public interface UserMapper {

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "avatar", expression = "java(decodeStringToBytes(entity.getAvatar()))"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "role", source = "role"),
            @Mapping(target = "favoriteProducts", source = "favoriteProducts"),
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

    default byte[] encodeBytesToString(final MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default String decodeStringToBytes(final byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

}
