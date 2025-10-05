package com.example.userblservice.controllers;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.dto.user.UserSearchDto;
import com.example.userblservice.entity.user.UserCard;
import com.example.userblservice.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping("/save")
    public UserDto saveUser(@ModelAttribute("userAuth") final UserDto dto) {
        return userService.save(dto);
    }

    @GetMapping("/{page}/{size}")
    public Page<UserDto> getAllUsers(@PathVariable("page") final Integer page,
                                     @PathVariable("size") final Integer size,
                                     @RequestParam(value = "name", required = false) final String name) {
        final UserSearchDto dto = UserSearchDto.builder()
                .name(name)
                .build();
        return userService.findAll(page, size, dto);
    }

    @PostMapping("/find")
    public boolean findExists(@RequestParam("find") final String name) {
        return userService.findByName(name);
    }

    @PostMapping("/login")
    public boolean login(@RequestParam("nameAuth") final String authName, @RequestParam("password") final String password) {
        return userService.checkExists(authName, password);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") final UUID id) {
        userService.delete(id);
    }

    @GetMapping("/{user_id}")
    public UserDto personalUser(@PathVariable("user_id") final UUID id) {
        return userService.getByid(id);
    }

    @PostMapping("/{user_id}/add_favorite/{prod_id}")
    public void addToFavorite(@PathVariable("user_id") final UUID user_id,
                              @PathVariable("prod_id") final Integer prod_id) {
        userService.addToFavorite(user_id, prod_id);
    }

    @DeleteMapping("/{user_id}/remove_favorite/{prod_id}")
    public void removeFavorite(@PathVariable("user_id") final UUID user_id,
                               @PathVariable("prod_id") final Integer prod_id) {
        userService.removeFavorite(user_id, prod_id);
    }

    @PostMapping("/{user_id}/comment/{product_id}")
    public void leaveCommentary(@PathVariable("user_id") final UUID user_id,
                                @PathVariable("product_id") final Integer prod_id,
                                @RequestParam("commentary") final String comment) {
        userService.addComment(user_id, prod_id, comment);
    }

    @PostMapping("/card/{user_id}")
    public UserCard addNewCard(@PathVariable("user_id") final UUID user_id) {
        return userService.addCard(user_id);
    }

    @PostMapping("/deleteComment/{comment_id}")
    void deleteComm(@PathVariable("comment_id") final Integer id) {
        userService.deleteCommentary(id);
    }
}
