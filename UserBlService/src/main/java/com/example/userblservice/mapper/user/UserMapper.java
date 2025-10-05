package com.example.userblservice.mapper.user;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.mapper.product.ProductImageMapper;
import com.example.userblservice.mapper.product.ProductMapper;
import lombok.Getter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
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

    public byte[] setDefaultAvatar(){
        try {
            final BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/defaultAvatar.jpg")));
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String decodeStringToBytes(final byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

    public String encodePassword(final UserDto dto){
        return encoder.encode(dto.getPassword());
    }

}
