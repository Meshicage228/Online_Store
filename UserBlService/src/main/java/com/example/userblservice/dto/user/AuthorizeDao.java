package com.example.userblservice.dto.user;

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
    @NotEmpty(message = "Введите ваш логин")
    @Size(min = 5, max = 15, message = "Логин в размере от 5 до 15 символов")
    private String nameAuth;
    @Size(min = 5, max = 30, message = "Пароль в размере от 5 до 30 символов")
    @NotEmpty(message = "Введите пароль")
    private String password;
    @NotEmpty(message = "Подтвердите пароль")
    private String doublePass;
}
