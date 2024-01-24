package com.example.adminblservice.controllers;

import com.example.adminblservice.dto.product.ProductDto;
import com.example.adminblservice.dto.product.ProductSearchDto;
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
@RequestMapping("/v1/products")
public class ProductController {
    ProductServiceImpl service;

    @GetMapping("/sorted/{page}/{size}")
    public Page<ProductDto> getAllSearchPaginatedSortedProducts
            (@PathVariable(required = false, name = "page") Integer page,
             @PathVariable(required = false, name = "size") Integer size,
             @RequestParam(required = false, name = "price") Float price,
             @RequestParam(required = false, name = "title") String title,
             @RequestParam(required = false, defaultValue = "defaultOrder", name = "sortedBy") String sortedBy) {
        ProductSearchDto build = ProductSearchDto.builder()
                .title(title)
                .price(price)
                .build();
        return service.getAllProducts(page, size, sortedBy, build);
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
    ProductDto saveProduct(@ModelAttribute ProductDto dto) {
        return service.save(dto);
    }
}
