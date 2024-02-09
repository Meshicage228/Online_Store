package com.example.coursework.dto.user;

import com.example.coursework.utils.markers.AuthorizeValidationMarker;
import com.example.coursework.utils.markers.LoginValidationMarker;
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
    @NotEmpty(message = "Введите ваш логин", groups = {AuthorizeValidationMarker.class, LoginValidationMarker.class})
    @Size(min = 5, max = 15, message = "Логин в размере от 5 до 15 символов", groups = {AuthorizeValidationMarker.class, LoginValidationMarker.class})
    private String nameAuth;
    @Size(min = 5, max = 30, message = "Пароль в размере от 5 до 30 символов", groups = {AuthorizeValidationMarker.class, LoginValidationMarker.class})
    @NotEmpty(message = "Введите пароль", groups = {AuthorizeValidationMarker.class, LoginValidationMarker.class})
    private String password;
    @NotEmpty(message = "Подтвердите пароль", groups = AuthorizeValidationMarker.class)
    private String doublePass;
}
