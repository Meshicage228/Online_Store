package com.example.coursework.clients;

import com.example.coursework.configuration.ProjectConfig;
import com.example.coursework.dto.product.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@FeignClient(name = "${app.clients.admin.name}",
        url = "${app.clients.admin.url}",
        path = "${app.clients.admin.product-path}",
        configuration = {ProjectConfig.class})
public interface AdminClient {
    @GetMapping("/sorted/{page}/{size}")
    Page<ProductDto> getAllSearchPaginatedSortedProducts(@PathVariable("page") Integer page,
                                                         @PathVariable("size") Integer size,
                                                         @RequestParam(name = "title", required = false) String title,
                                                         @RequestParam(name = "price", required = false) Float price,
                                                         @RequestParam(name = "sortedBy", required = false) String sortedBy);

    @GetMapping("/{id}")
    ProductDto findProductById(@PathVariable("id") Integer id);

    @PutMapping("/{id}/change")
    ProductDto update(@PathVariable("id") Integer id,
                      @RequestBody ProductDto dto);

    @DeleteMapping("/{id}/delete")
    void deleteProduct(@PathVariable("id") Integer id);

    @DeleteMapping("/image/{image_id}/delete")
    void deleteImage(@PathVariable("image_id") Integer imageId);

    @PatchMapping(path = "/image/{image_id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void updatePicture(@PathVariable("image_id") Integer idImage,
                       @RequestPart("file") MultipartFile file);

    // TODO: 17.01.2024 Починить
    @PostMapping(path = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    ProductDto saveProduct(@ModelAttribute ProductDto dto);
}
