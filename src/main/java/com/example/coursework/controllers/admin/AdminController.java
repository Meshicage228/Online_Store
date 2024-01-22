package com.example.coursework.controllers.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String adminPage() {
        return "redirect:/admin/products/0/10";
    }

    @GetMapping("/createProduct")
    public String createProductPage() {
        return "createProductAdmin";
    }
    @GetMapping("/users")
    public String getAllUsers(){
        return "redirect:/admin/users/0/10";
    }
}
