package com.example.coursework.clients;

import com.example.coursework.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "${app.clients.users.name}",
        url = "${app.clients.users.url}" ,
        path = "${app.clients.users.user-path}"
)
public interface UsersClient {
    @GetMapping("/{page}/{size}")
    Page<UserDto> getAllUsers(@PathVariable("page") Integer page,
                              @PathVariable("size") Integer size,
                              @RequestParam(value = "name", required = false) String name);
    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable("id") UUID id);
}
