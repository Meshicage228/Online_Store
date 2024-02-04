package com.example.userblservice.mapper.user;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.mapper.product.ProductImageMapper;
import com.example.userblservice.mapper.product.ProductMapper;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;


@Mapper(
        componentModel = "spring",
        uses = {ProductMapper.class, ProductImageMapper.class}
)
public abstract class UserMapper {

/*    @Autowired
    private BCryptPasswordEncoder encoder;*/

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "avatar", /*expression = "java(decodeStringToBytes(entity.getAvatar()))"*/ ignore = true),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "role", source = "role"),
            @Mapping(target = "favoriteProducts", source = "favoriteProducts"),
            @Mapping(target = "basket", source = "cart")
    })
    public abstract UserDto toDto(UserEntity entity);
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            // TODO: 03.02.2024 commited avater while saving in /store/authorize
            @Mapping(target = "avatar", /*expression = "java(encodeBytesToString(dto.getFile()))"*/ ignore = true),
            @Mapping(target = "password", /*expression = "java(encodePassword(dto))"*/ source = "password"),
            @Mapping(target = "role", defaultValue = "USER")
    })
    public abstract UserEntity toEntity(UserDto dto);


    public abstract List<UserDto> toDtos(List<UserEntity> dtos);

    // TODO: 21.01.2024 Resolve update avatar and password
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "password", source = "password")
    public abstract UserEntity update(@MappingTarget UserEntity target, UserEntity source);

    public byte[] encodeBytesToString(MultipartFile file){
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
/*    public String decodeStringToBytes(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }*/

/*    public String encodePassword(UserDto dto){
        return encoder.encode(dto.getPassword());
    }*/

}
