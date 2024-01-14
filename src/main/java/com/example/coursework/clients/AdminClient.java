package com.example.coursework.clients;

import com.example.coursework.configuration.ProjectConfig;
import com.example.coursework.domain.ProductSearchDto;
import com.example.coursework.dto.ProductDto;
import com.example.coursework.entity.ProductEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


@FeignClient(name = "${app.clients.admin.name}",
             url = "${app.clients.admin.url}",
             path = "${app.clients.admin.product-path}",
             configuration = ProjectConfig.class)
public interface AdminClient {
    @GetMapping("/{page}/{size}")
    Page<ProductEntity> findAll(@PathVariable(value = "page") Integer page,
                                @PathVariable(value = "size") Integer size,
                                ProductSearchDto search);

    @GetMapping("/{id}")
    ProductDto findProductById(@PathVariable("id") Integer id);

    @PutMapping("/{id}/change")
    ProductDto update(@PathVariable("id") Integer id,
                        ProductDto dto);

    @DeleteMapping("/{id}/delete")
    void deleteProduct(@PathVariable("id") Integer id);

    @DeleteMapping("/image/{image_id}/delete")
    void deleteImage(@PathVariable("image_id") Integer imageId);

    @PatchMapping("/image/{image_id}/update")
    ModelAndView updatePicture(@PathVariable("image_id") Integer idImage,
                               @RequestParam("newImage") MultipartFile file);

    @PostMapping("/save")
    ProductDto save(ProductDto dto,
                @RequestParam(value = "file") MultipartFile file);
}
