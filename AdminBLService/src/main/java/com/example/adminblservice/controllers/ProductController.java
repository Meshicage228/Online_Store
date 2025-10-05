package com.example.adminblservice.controllers;

import com.example.adminblservice.dto.product.ProductDto;
import com.example.adminblservice.dto.product.ProductSearchDto;
import com.example.adminblservice.service.impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductController {
    private final ProductServiceImpl service;

    @GetMapping("/sorted/{page}/{size}")
    public Page<ProductDto> getAllSearchPaginatedSortedProducts
            (@PathVariable(required = false, name = "page") final Integer page,
             @PathVariable(required = false, name = "size") final Integer size,
             @RequestParam(required = false, name = "price") final Float price,
             @RequestParam(required = false, name = "title") final String title,
             @RequestParam(required = false, defaultValue = "defaultOrder", name = "sortedBy") final String sortedBy) {
        final ProductSearchDto build = ProductSearchDto.builder()
                .title(title)
                .price(price)
                .build();
        return service.getAllProducts(page, size, sortedBy, build);
    }

    @GetMapping("/{id}")
    public ProductDto findProductById(@PathVariable final Integer id) {
        return service.findByIdProduct(id);
    }

    @PutMapping("/{id}/change")
    public ProductDto update(@PathVariable("id") final Integer id,
                             @RequestBody final ProductDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}/delete")
    public void deleteProduct(@PathVariable("id") final Integer id) {
        service.delete(id);
    }

    @DeleteMapping("/image/{image_id}/delete")
    public void deleteImage(@PathVariable("image_id") final Integer imageId) {
        service.deleteImage(imageId);
    }

    @PostMapping("/image/{image_id}/update")
    public void updatePicture(@PathVariable("image_id") final Integer idImage,
                              @RequestPart("file") final MultipartFile file) {
        service.update(idImage, file);
    }

    @PostMapping("/save")
    ProductDto saveProduct(@RequestBody final ProductDto dto) {
        return service.save(dto);
    }

    @PostMapping(value = "/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    void addNewImage(@PathVariable("id") final Integer id,
                     @RequestPart("file") final MultipartFile file) {
        service.addImage(id, file);
    }
}
