package com.example.coursework.controllers.admin;

import com.example.coursework.dto.ProductDto;
import com.example.coursework.service.impl.ProductServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/admin")
public class AdminController {
    ProductServiceImpl productService;

    @GetMapping
    public String adminPage(){
        return "redirect:/admin/products/1/5";
    }
    @GetMapping("/createProduct")
    public String createProductPage(){
        return "createProductAdmin";
    }
   /* @PatchMapping("/imageUpdate")
    public String updateImage(@RequestParam("id") Integer id,
                              @RequestParam ("file") MultipartFile file){
        productService.update();
    }*/
    @PostMapping("/createProduct/save")
    public String save(ProductDto dto,
                       @RequestParam(value = "file") MultipartFile file){
        productService.save(dto, file);
        return "redirect:/admin/createProduct";
    }
}
