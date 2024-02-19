package com.example.coursework.controllers.products;

import com.example.coursework.clients.ProductClient;
import com.example.coursework.dto.product.ProductDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

@Controller
@RequestMapping("*/products")
public class ProductsController {
    ProductClient adminClient;

    @GetMapping("/{page}/{size}")
    public ModelAndView getPage(@PathVariable(value = "page") Integer page,
                                @PathVariable(value = "size") Integer size,
                                @RequestParam(value = "title", required = false) String title,
                                @RequestParam(value = "price", required = false) Float price,
                                @RequestParam(value = "sortedBy", required = false) String sortedBy) {

        ModelAndView modelAndView = new ModelAndView("adminPage");
        Page<ProductDto> pageContent = adminClient.getAllSearchPaginatedSortedProducts(page, size, title, price, sortedBy);

        modelAndView.addObject("totalPage", pageContent);
        int totalPages = pageContent.getTotalPages();
        if (totalPages > 0) {
            List<Integer> countOfButtons = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            modelAndView.addObject("countPages", countOfButtons);
        }
        modelAndView.addObject("sort", sortedBy);
        modelAndView.addObject("searchTitle", title);
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getById(@PathVariable("id") Integer id,
                                @ModelAttribute("modelToUpdate") ProductDto showDto) {
        ProductDto dto = adminClient.findProductById(id);
        return new ModelAndView("adminUpdateProduct").addObject("modelToUpdate", dto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/change")
    public ModelAndView update(@PathVariable("id") Integer id,
                               @Valid @ModelAttribute("modelToUpdate") ProductDto dto,
                               BindingResult check) {
        if(!check.hasFieldErrors()) {
            ProductDto updated = adminClient.update(id, dto);
            return new ModelAndView("redirect:/admin/products/" + id).addObject("modelToUpdate", updated);
        }
        return new ModelAndView("adminUpdateProduct");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}/delete")
    public String deleteProduct(@PathVariable("id") Integer id) {
        adminClient.deleteProduct(id);
        return "redirect:/admin";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{product_id}/image/{image_id}/delete")
    public String deleteImage(@PathVariable("product_id") Integer productId,
                              @PathVariable("image_id") Integer imageId) {
        adminClient.deleteImage(imageId);
        return "redirect:/admin/products/" + productId;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{product_id}/image/{image_id}/update")
    public ModelAndView updatePicture(@PathVariable("product_id") Integer idProduct,
                                      @PathVariable(value = "image_id") Integer idImage,
                                      @RequestParam("newImage") MultipartFile file) {
        if (file.getSize() != 0) {
            adminClient.updatePicture(idImage, file);
        }
        return new ModelAndView("redirect:/admin/products/" + idProduct);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{prod_id}/addImage")
    public ModelAndView saveNewImage(@PathVariable("prod_id") Integer prodId,
                                     @RequestParam("addImage") MultipartFile file) {
        adminClient.addNewImage(prodId, file);

        return new ModelAndView("redirect:/admin/products/" + prodId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/save")
    public ModelAndView save(@Valid @ModelAttribute(name = "modelToSave") ProductDto dto,
                             BindingResult check) {
        ModelAndView modelAndView = new ModelAndView("createProductAdmin");
        if (!check.hasFieldErrors()) {
            List<byte[]> imagesToThrow = new ArrayList<>();

            dto.getFileImage().forEach(image -> {
                try {
                    imagesToThrow.add(image.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            dto.setFileImage(null);
            dto.setImagesToThrow(imagesToThrow);
            adminClient.saveProduct(dto);
            modelAndView.addObject("successSave", "Товар успешно сохранен!");
            return modelAndView.addObject("modelToSave", new ProductDto());
        }
        modelAndView.addObject("modelToSave", dto);
        return modelAndView;
    }
}
