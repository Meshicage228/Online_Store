package com.example.coursework.dto.user;

import com.example.coursework.domain.Role;
import com.example.coursework.dto.product.ProductDto;
import com.example.coursework.utils.annotations.CheckFileSize;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private UUID id;
    @Size(min = 5, max = 20, message = "Пароль в размере от 5 до 20 символов")
    private String password;
    private String name;
    private Role role;
    @CheckFileSize(maxSizeInMB = 5, message = "{app.file-size-message}")
    private MultipartFile file;
    private String avatar;
    private List<ProductDto> basket;
    private List<ProductDto> favoriteProducts;
}
