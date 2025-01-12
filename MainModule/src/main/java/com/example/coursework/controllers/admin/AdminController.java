package com.example.coursework.controllers.admin;

import com.example.coursework.dto.product.ProductDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @GetMapping
    public String adminPage() {
        return "redirect:/admin/orders/0/10";
    }

    @GetMapping("/createProduct")
    public String createProductPage(@ModelAttribute("modelToSave") ProductDto dto) {
        return "createProductAdmin";
    }
}
