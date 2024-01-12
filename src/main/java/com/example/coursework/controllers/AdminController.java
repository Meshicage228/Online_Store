package com.example.coursework.controllers;

import com.example.coursework.domain.ProductSearchDto;
import com.example.coursework.dto.ProductDto;
import com.example.coursework.entity.ProductEntity;
import com.example.coursework.mappers.ProductMapper;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/admin")
public class AdminController {
    ProductServiceImpl productService;

    @GetMapping
    public String adminPage(){
        return "storePage";
    }
    @PutMapping("/update")
    public String update(ProductDto dto,
                         @RequestParam("file") MultipartFile file){
        productService.update(dto, file);
        return "admin";
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
        ModelAndView modelAndView = new ModelAndView("result");
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
    @PostMapping("/save")
    public String save(ProductDto dto,
                       @RequestParam(value = "file") MultipartFile file){
        productService.save(dto, file);
        return "redirect:/admin";
    }
}
