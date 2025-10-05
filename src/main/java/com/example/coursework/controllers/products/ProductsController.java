package com.example.coursework.controllers.products;

import com.example.coursework.clients.ProductClient;
import com.example.coursework.dto.product.ProductDto;
import com.example.coursework.utils.markers.OnCreateMarker;
import com.example.coursework.utils.markers.OnUpdateMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductsController {
    private final ProductClient productClient;

    @GetMapping("/{id}")
    public ModelAndView getById(@PathVariable("id") final Integer id,
                                @ModelAttribute("modelToUpdate") final ProductDto showDto) {
        final ProductDto dto = productClient.findProductById(id);
        return new ModelAndView("adminUpdateProduct").addObject("modelToUpdate", dto);
    }

    @PutMapping("/{id}/change")
    public ModelAndView update(@PathVariable("id") final Integer id,
                               @Validated(OnUpdateMarker.class) @ModelAttribute("modelToUpdate") final ProductDto dto,
                               final BindingResult check) {
        if (!check.hasFieldErrors()) {
            final ProductDto updated = productClient.update(id, dto);
            return new ModelAndView("redirect:/admin/products/" + id).addObject("modelToUpdate", updated);
        }
        final ProductDto productById = productClient.findProductById(id);
        return new ModelAndView("adminUpdateProduct").addObject("modelToUpdate", productById);
    }

    @DeleteMapping("/{id}/delete")
    public String deleteProduct(@PathVariable("id") final Integer id) {
        productClient.deleteProduct(id);
        return "redirect:/store/0/10";
    }

    @DeleteMapping("/{product_id}/image/{image_id}/delete")
    public String deleteImage(@PathVariable("product_id") final Integer productId,
                              @PathVariable("image_id") final Integer imageId) {
        productClient.deleteImage(imageId);
        return "redirect:/admin/products/" + productId;
    }

    @PostMapping("/{product_id}/image/{image_id}/update")
    public ModelAndView updatePicture(@PathVariable("product_id") final Integer idProduct,
                                      @PathVariable(value = "image_id") final Integer idImage,
                                      @RequestParam("newImage") final MultipartFile file) {
        if (file.getSize() == 0) {
            return getById(idProduct, new ProductDto()).addObject("emptyImageUpdate", "Выберите изображение!");
        }
        productClient.updatePicture(idImage, file);
        return new ModelAndView("redirect:/admin/products/" + idProduct);
    }

    @PostMapping("/{prod_id}/addImage")
    public ModelAndView saveNewImage(@PathVariable("prod_id") final Integer prodId,
                                     @RequestParam("addImage") final MultipartFile file) {

        if (file.getSize() == 0) {
            return getById(prodId, new ProductDto()).addObject("emptyImage", "Выберите новое изображение!");
        }
        productClient.addNewImage(prodId, file);

        return new ModelAndView("redirect:/admin/products/" + prodId);
    }

    @PostMapping("/save")
    public ModelAndView save(@Validated(OnCreateMarker.class) @ModelAttribute(name = "modelToSave") final ProductDto dto,
                             final BindingResult check) {
        final ModelAndView modelAndView = new ModelAndView("createProductAdmin");
        if (!check.hasFieldErrors()) {
            final List<byte[]> imagesToThrow = new ArrayList<>();

            dto.getFileImage().forEach(image -> {
                try {
                    imagesToThrow.add(image.getBytes());
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            });
            dto.setFileImage(null);
            dto.setImagesToThrow(imagesToThrow);
            productClient.saveProduct(dto);
            modelAndView.addObject("successSave", "Товар успешно сохранен!");
            return modelAndView.addObject("modelToSave", new ProductDto());
        }
        modelAndView.addObject("modelToSave", dto);
        return modelAndView;
    }
}
