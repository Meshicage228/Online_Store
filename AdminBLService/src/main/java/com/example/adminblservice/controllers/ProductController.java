package com.example.adminblservice.controllers;

import com.example.adminblservice.dto.ProductDto;
import com.example.adminblservice.dto.ProductSearchDto;
import com.example.adminblservice.entity.ProductEntity;
import com.example.adminblservice.service.impl.ProductServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("/products")
public class ProductController {
    ProductServiceImpl service;

    @GetMapping("/{page}/{size}")
    public Page<ProductEntity> findAll(@PathVariable(value = "page") Integer page,
                                       @PathVariable(value = "size") Integer size,
                                       ProductSearchDto search) {
        return service.findAll(page, size, search);
    }

    @GetMapping("/{id}")
    public ProductDto findProductById(@PathVariable("id") Integer id) {
        return service.findByIdProduct(id);
    }

    @PutMapping("/{id}/change")
    public ProductDto update(@PathVariable("id") Integer id,
                             ProductDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}/delete")
    public void deleteProduct(@PathVariable("id") Integer id) {
        service.delete(id);
    }

    @DeleteMapping("/image/{image_id}/delete")
    public void deleteImage(@PathVariable("image_id") Integer imageId) {
        service.deleteImage(imageId);
    }

    @PatchMapping("/image/{image_id}/update")
    public void updatePicture(@PathVariable("image_id") Integer idImage,
                              @RequestParam("newImage") MultipartFile file) {
        if (file.getSize() != 0) {
            service.update(idImage, file);
        }
    }

    @PostMapping("/save")
    public ProductDto save(ProductDto dto,
                           @RequestParam(value = "file") MultipartFile file) {
        return service.save(dto, file);
    }
}
