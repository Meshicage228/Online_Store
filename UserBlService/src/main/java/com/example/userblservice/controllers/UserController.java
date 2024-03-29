package com.example.userblservice.controllers;

import com.example.userblservice.dto.user.UserDto;
import com.example.userblservice.dto.user.UserSearchDto;
import com.example.userblservice.entity.user.UserCard;
import com.example.userblservice.service.impl.UserServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@RestController
@RequestMapping("/v1/users")
public class UserController {
    UserServiceImpl userService;

    @PostMapping("/save")
    public UserDto saveUser(@ModelAttribute("userAuth") UserDto dto) {
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
    public boolean findExists(@RequestParam("find") String name) {
        return userService.findByName(name);
    }
    @PostMapping("/login")
    public boolean login(@RequestParam("nameAuth") String authName, @RequestParam("password") String password){
        return userService.checkExists(authName,password);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") UUID id) {
        userService.delete(id);
    }

    @GetMapping("/{user_id}")
    public UserDto personalUser(@PathVariable("user_id") UUID id) {
        return userService.getByid(id);
    }

    @PostMapping("/{user_id}/add_favorite/{prod_id}")
    public void addToFavorite(@PathVariable("user_id") UUID user_id,
                              @PathVariable("prod_id") Integer prod_id) {
        userService.addToFavorite(user_id, prod_id);
    }
    @DeleteMapping("/{user_id}/remove_favorite/{prod_id}")
    public void removeFavorite(@PathVariable("user_id") UUID user_id,
                               @PathVariable("prod_id") Integer prod_id){
        userService.removeFavorite(user_id, prod_id);
    }

    @PostMapping("/{user_id}/comment/{product_id}")
    public void leaveCommentary(@PathVariable("user_id") UUID user_id,
                                      @PathVariable("product_id") Integer prod_id,
                                      @RequestParam("commentary") String comment) {
        userService.addComment(user_id, prod_id, comment);
    }

    @PostMapping("/card/{user_id}")
    public UserCard addNewCard(@PathVariable("user_id") UUID user_id) {
        return userService.addCard(user_id);
    }

    @PostMapping("/deleteComment/{comment_id}")
    void deleteComm(@PathVariable("comment_id") Integer id){
        userService.deleteCommentary(id);
    }
}
