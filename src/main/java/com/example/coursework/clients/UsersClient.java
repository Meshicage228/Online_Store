package com.example.coursework.clients;

import com.example.coursework.configuration.ProjectConfig;
import com.example.coursework.dto.user.AuthorizeDao;
import com.example.coursework.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
        name = "${app.clients.users.name}",
        url = "${app.clients.users.url}",
        path = "${app.clients.users.user-path}",
        configuration = ProjectConfig.class
)
public interface UsersClient {
    @PostMapping(value = "/save", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    UserDto saveUser(@ModelAttribute UserDto dto);

    @GetMapping("/{page}/{size}")
    Page<UserDto> getAllUsers(@PathVariable("page") Integer page,
                              @PathVariable("size") Integer size,
                              @RequestParam(value = "name", required = false) String name);
    @PostMapping("/find")
    boolean findExists(@RequestParam("find") String name);
    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable("id") UUID id);

    @GetMapping("/{user_id}")
    UserDto personalUser(@PathVariable("user_id") UUID id);

    @PutMapping("/{user_id}")
    UserDto updateUser(@PathVariable("user_id") UUID id,
                       @ModelAttribute UserDto dto);

    @PostMapping("/{user_id}/add_favorite/{prod_id}")
    void addToFavorite(@PathVariable("user_id") UUID user_id,
                       @PathVariable("prod_id") Integer prod_id);

    @PostMapping("/{user_id}/comment/{product_id}")
    void leaveCommentary(@PathVariable("user_id") UUID user_id,
                         @PathVariable("product_id") Integer prod_id,
                         @RequestParam("commentary") String comment);

    @PostMapping("/card/{user_id}")
    void addNewCard(@PathVariable("user_id") UUID user_id);

    @PostMapping("/login")
    void login(@RequestParam("nameAuth") String authName, @RequestParam("password") String password);
}
