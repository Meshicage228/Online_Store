package com.example.adminblservice.controllers;

import com.example.adminblservice.dto.ProductDto;
import com.example.adminblservice.dto.ProductSearchDto;
import com.example.adminblservice.service.impl.ProductServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@RestController
@RequestMapping("/products")
public class ProductController {
    ProductServiceImpl service;

    @GetMapping("/{page}/{size}")
    public Page<ProductDto> getPage(@PathVariable(value = "page") Integer page,
                                    @PathVariable(value = "size") Integer size,
                                    @RequestParam(value = "title", required = false)  String title,
                                    @RequestParam(value = "price", required = false) Float price) {
        ProductSearchDto build = ProductSearchDto.builder()
                .title(title)
                .price(price)
                .build();
        return service.findAll(page, size, build);
    }

    @GetMapping("/{id}")
    public ProductDto findProductById(@PathVariable Integer id) {
        return service.findByIdProduct(id);
    }

    @PutMapping("/{id}/change")
    public ProductDto update(@PathVariable("id") Integer id,
                             @RequestBody ProductDto dto) {
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
                              @RequestPart("file") MultipartFile file) {
        service.update(idImage, file);
    }

    @PostMapping("/save")
    public ProductDto saveProduct(@ModelAttribute ProductDto dto) {
        return service.save(dto);
    }
}
