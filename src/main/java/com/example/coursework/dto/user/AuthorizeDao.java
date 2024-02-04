package com.example.coursework.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthorizeDao {
    @Size(min = 2, max = 30, message = "Имя от 2 до 30 символов")
    @NotEmpty(message = "Введите имя")
    private String name;
    @Size(min = 5, max = 30, message = "Пароль в размере от 5 до 30 символов")
    @NotEmpty(message = "Введите пароль")
    private String password;
    @NotEmpty(message = "Подтвердите пароль")
    private String doublePass;
}
