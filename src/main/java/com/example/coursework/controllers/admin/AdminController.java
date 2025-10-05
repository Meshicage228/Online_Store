package com.example.coursework.controllers.admin;

import com.example.coursework.dto.product.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @GetMapping
    public String adminPage() {
        return "redirect:/admin/orders/0/10";
    }

    @GetMapping("/createProduct")
    public String createProductPage(@ModelAttribute("modelToSave") final ProductDto dto) {
        return "createProductAdmin";
    }
}
