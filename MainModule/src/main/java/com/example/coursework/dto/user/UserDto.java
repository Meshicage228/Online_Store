package com.example.coursework.dto.user;

import com.example.coursework.domain.Role;
import com.example.coursework.dto.product.ProductDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDto {
    private UUID id;
    @NotEmpty(message = "Введите пароль")
    @Size(min = 5, max = 30, message = "Пароль в размере от 5 до 30 символов")
    private String password;

    @NotEmpty(message = "Введите ваш логин")
    @Size(min = 5, max = 15, message = "Логин в размере от 5 до 15 символов")
    private String name;

    private Role role;
    private MultipartFile file;
    private String avatar;
    private byte[] imageToService;
    private List<ProductDto> basket;
    private List<ProductDto> favoriteProducts;
}
