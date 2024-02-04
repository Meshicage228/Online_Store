package com.example.userblservice.controllers;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.dto.user.UserSearchDto;
import com.example.userblservice.entity.user.UserEntity;
import com.example.userblservice.service.impl.UserServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
/*import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;*/
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/users")
public class UserController {
    UserServiceImpl userService;

    @PostMapping("/save")
    public UserDto saveUser(@ModelAttribute UserDto dto) {
        return userService.save(dto);
    }

    @GetMapping("/{page}/{size}")
    public Page<UserDto> getAllUsers(@PathVariable("page") Integer page,
                                     @PathVariable("size") Integer size,
                                     @RequestParam(value = "name", required = false) String name) {
        UserSearchDto dto = UserSearchDto.builder()
                .name(name)
                .build();
        return userService.findAll(page, size, dto);
    }

    @PostMapping("/find")
    public boolean findExists(@RequestParam("username") String name) {
        boolean enabled = userService.findByName(name);
        return enabled;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") UUID id) {
        userService.delete(id);
    }

    @GetMapping("/{user_id}")
    public UserDto personalUser(@PathVariable("user_id") UUID id) {
        return userService.getByid(id);
    }

    @PutMapping("/{user_id}")
    public UserDto updateUser(@PathVariable("user_id") UUID id,
                              @ModelAttribute UserDto dto) {
        return userService.update(id, dto);
    }

    @PostMapping("/{user_id}/add_favorite/{prod_id}")
    public void addToFavorite(@PathVariable("user_id") UUID user_id,
                              @PathVariable("prod_id") Integer prod_id) {
        userService.addToFavorite(user_id, prod_id);
    }

    @PostMapping("/{user_id}/comment/{product_id}")
    public void leaveCommentary(@PathVariable("user_id") UUID user_id,
                                @PathVariable("product_id") Integer prod_id,
                                @RequestParam("commentary") String comment) {
        userService.addComment(user_id, prod_id, comment);
    }

    @PostMapping("/card/{user_id}")
    public void addNewCard(@PathVariable("user_id") UUID user_id) {
        userService.addCard(user_id);
    }
}
