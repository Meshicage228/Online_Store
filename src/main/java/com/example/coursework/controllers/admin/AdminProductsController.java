package com.example.coursework.controllers.admin;

import com.example.coursework.clients.AdminClient;
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
import java.util.stream.IntStream;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("*/products")
public class AdminProductsController {
    AdminClient adminClient;

    @GetMapping("/{page}/{size}")
    public ModelAndView getPage(@PathVariable(value = "page") Integer page,
                                @PathVariable(value = "size") Integer size,
                                ProductSearchDto search) {

        ModelAndView modelAndView = new ModelAndView("adminPage");
        Page<ProductEntity> pageContent = adminClient.findAll(page, size, search);
        modelAndView.addObject("totalPage", pageContent);
        int totalPages = pageContent.getTotalPages();
        if (totalPages > 0) {
            List<Integer> countOfButtons = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelAndView.addObject("countPages", countOfButtons);
        }

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getById(@PathVariable("id") Integer id) {
        ProductDto dto = adminClient.findProductById(id);
        return new ModelAndView("productChangeAdmin").addObject("toChange", dto);
    }

    @PutMapping("/{id}/change")
    public ModelAndView update(@PathVariable("id") Integer id,
                               ProductDto dto) {
        ProductDto updated = adminClient.update(id, dto);
        return new ModelAndView("redirect:/admin/products/" + id).addObject("toChange", updated);
    }
    @DeleteMapping("/{id}/delete")
    public String deleteProduct(@PathVariable("id")Integer id){
        adminClient.deleteProduct(id);
        return "redirect:/admin";
    }
    @DeleteMapping("/{product_id}/image/{image_id}/delete")
    public String deleteImage(@PathVariable("product_id")Integer productId,
                              @PathVariable("image_id")Integer imageId){
        adminClient.deleteImage(imageId);
        return "redirect:/admin/products/" + productId;
    }

    @PatchMapping("/{product_id}/image/{image_id}/update")
    public ModelAndView updatePicture(@PathVariable("product_id") Integer idProduct,
                                      @PathVariable("image_id") Integer idImage,
                                      @RequestParam("newImage") MultipartFile file){
        if(file.getSize() != 0) {
            adminClient.updatePicture(idImage, file);
        }
        return new ModelAndView("redirect:/admin/products/" + idProduct);
    }
    @PostMapping("/save")
    public String save(ProductDto dto,
                       @RequestParam(value = "file") MultipartFile file){
        adminClient.save(dto, file);
        return "redirect:/admin/createProduct";
    }
}
