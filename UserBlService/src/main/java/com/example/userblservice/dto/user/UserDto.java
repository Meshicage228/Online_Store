package com.example.userblservice.dto.user;

import com.example.userblservice.domain.Role;
import com.example.userblservice.dto.product.ProductDto;
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
    private String password;
    private String name;
    private Role role;
    private MultipartFile file;
    private String avatar;
    private List<ProductDto> basket;
    private List<ProductDto> favorite;
}
