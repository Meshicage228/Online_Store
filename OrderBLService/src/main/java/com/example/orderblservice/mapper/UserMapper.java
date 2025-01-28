package com.example.orderblservice.mapper;

import com.example.orderblservice.dto.user.UserDto;
import com.example.orderblservice.entity.user.UserEntity;
import org.mapstruct.*;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
         @Mapping(target = "id", source = "id"),
         @Mapping(target = "name", source = "name"),
         @Mapping(target = "avatar", expression = "java(decodeStringToBytes(entity.getAvatar()))"),
         @Mapping(target = "password", source = "password"),
         @Mapping(target = "role", source = "role")
    })
    UserDto toDto(UserEntity entity);

    default String decodeStringToBytes(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

}
