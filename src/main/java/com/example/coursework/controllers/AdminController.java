package com.example.coursework.controllers;

import com.example.coursework.dto.ProductDto;
import com.example.coursework.entity.ProductEntity;
import com.example.coursework.service.impl.ProductServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/admin")
public class AdminController {
    ProductServiceImpl productService;

    @GetMapping
    public ModelAndView getAdminPage(){
        return new ModelAndView("admin").addObject("products",productService.findAll());
    }
    @PostMapping("/save")
    public String save(ProductDto dto,
                       @RequestParam(value = "file") MultipartFile file){
        productService.save(dto, file);

        return "admin";
    }
}
