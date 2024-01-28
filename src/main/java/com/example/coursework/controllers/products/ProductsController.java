package com.example.coursework.controllers.products;

import com.example.coursework.clients.AdminClient;
import com.example.coursework.dto.product.ProductDto;
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
public class ProductsController {
    AdminClient adminClient;

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

    // TODO: 20.01.2024  cannot be resolved to absolute file path
    @PostMapping( "/save")
    public String save(ProductDto dto){
        adminClient.saveProduct(dto);
        return "redirect:/admin/createProduct";
    }
}
