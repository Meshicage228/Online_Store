package com.example.userblservice.mapper.user;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.mapper.product.ProductImageMapper;
import com.example.userblservice.mapper.product.ProductMapper;
import lombok.Getter;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.processing.Generated;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;



@Mapper(
        componentModel = "spring",
        uses = {ProductMapper.class, ProductImageMapper.class}
)
@Getter
public abstract class UserMapper {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "avatar", expression = "java(decodeStringToBytes(entity.getAvatar()))"),
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "role", source = "role"),
            @Mapping(target = "favoriteProducts", source = "favoriteProducts"),
            @Mapping(target = "basket", source = "cart")
    })
    public abstract UserDto toDto(UserEntity entity);
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "avatar", expression = "java(setDefaultAvatar())"),
            @Mapping(target = "password", expression = "java(encodePassword(dto))"),
            @Mapping(target = "role", defaultValue = "USER")
    })
    public abstract UserEntity toEntity(UserDto dto);


    public abstract List<UserDto> toDtos(List<UserEntity> dtos);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "name", source = "name")
    @Mapping(target = "password", source = "password")
    public abstract UserEntity update(@MappingTarget UserEntity target, UserEntity source);
    public byte[] setDefaultAvatar(){
        try {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/defaultAvatar.jpg")));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public byte[] encodeBytesToString(MultipartFile file){
        try {
            return file.getBytes();
        } catch (NullPointerException e) {
            return new byte[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String decodeStringToBytes(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

    public String encodePassword(UserDto dto){
        return encoder.encode(dto.getPassword());
    }

}
