package com.example.coursework.controllers.admin;

import com.example.coursework.clients.UsersClient;
import com.example.coursework.dto.user.UserDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("*/users")
public class AdminUsersController {
    UsersClient client;

    @GetMapping("/{page}/{size}")
    ModelAndView getAllUsers(@PathVariable("page") Integer page,
                             @PathVariable("size") Integer size,
                             @RequestParam(value = "name", required = false) String name) {
        Page<UserDto> allUsers = client.getAllUsers(page, size, name);
        return new ModelAndView("adminUsersPage").addObject("page", allUsers);
    }

    @DeleteMapping("/{id}")
    public String  deleteUser(@PathVariable("id") UUID id){
        client.deleteUser(id);
        return "redirect:/admin/users";
    }
}
