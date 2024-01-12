package com.example.coursework.controllers;

import com.example.coursework.domain.ProductSearchDto;
import com.example.coursework.dto.ProductDto;
import com.example.coursework.entity.ProductEntity;
import com.example.coursework.service.impl.ProductServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/admin")
public class AdminController {
    ProductServiceImpl productService;

    @GetMapping("/admin")
    public String adminPage(){
        return "redirect:/admin/products/1/5";
    }
    @GetMapping("/createProduct")
    public String createProductPage(){
        return "createProductAdmin";
    }
    @PutMapping("/update")
    public String update(ProductDto dto,
                         @RequestParam("file") MultipartFile file){
        productService.update(dto, file);
        return "createProductAdmin";
    }
   /* @PatchMapping("/imageUpdate")
    public String updateImage(@RequestParam("id") Integer id,
                              @RequestParam ("file") MultipartFile file){
        productService.update();
    }*/
    @GetMapping("/products/{page}/{size}")
    public ModelAndView getPage(@PathVariable(value = "page") Optional<Integer> page,
                                @PathVariable(value = "size") Optional<Integer> size,
                                ProductSearchDto search){
        Integer pageNumber = page.orElse(1);
        Integer sizePage = size.orElse(5);
        ModelAndView modelAndView = new ModelAndView("adminPage");
        Page<ProductEntity> pageContent = productService.findAll(pageNumber, sizePage, search);
        modelAndView.addObject("totalPage", pageContent);
        int totalPages = pageContent.getTotalPages();
        if(totalPages > 0){
            List<Integer> countOfButtons = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelAndView.addObject("countPages",countOfButtons);
        }

        return modelAndView;
    }
    @PostMapping("/createProduct/save")
    public String save(ProductDto dto,
                       @RequestParam(value = "file") MultipartFile file){
        productService.save(dto, file);
        return "redirect:/admin/createProduct";
    }
}
