package com.example.orderblservice.dto.user;

import com.example.orderblservice.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private UUID id;
    private String password;
    private String name;
    private Role role;
    private String avatar;
}
