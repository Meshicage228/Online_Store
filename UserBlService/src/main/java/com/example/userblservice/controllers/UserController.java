package com.example.userblservice.controllers;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.dto.user.UserSearchDto;
import com.example.userblservice.service.impl.UserServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/users")
public class UserController {
    UserServiceImpl userService;
    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserDto saveUser(@ModelAttribute UserDto dto){
        return userService.save(dto);
    }
    @GetMapping("/{page}/{size}")
    public Page<UserDto> getAllUsers(@PathVariable("page") Integer page,
                                     @PathVariable("size") Integer size,
                                     @RequestParam(value = "name", required = false) String name){
        UserSearchDto dto = UserSearchDto.builder()
                .name(name)
                .build();
        return userService.findAll(page, size, dto);
    }
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id")UUID id){
        userService.delete(id);
    }
    @GetMapping("/{user_id}")
    public UserDto personalUser(@PathVariable("user_id") UUID id){
        return userService.getByid(id);
    }
    @PutMapping("/{user_id}")
    public UserDto updateUser(@PathVariable("user_id") UUID id,
                              @ModelAttribute UserDto dto){
        return userService.update(id, dto);
    }
}
