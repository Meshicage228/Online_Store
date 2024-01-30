package com.example.coursework.dto.product;

import com.example.coursework.utils.annotations.CheckFileSize;
import com.example.coursework.utils.annotations.CheckIsZero;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDto {
    private Integer id;

    @NotEmpty(message = "Вебирите 1 или более изображений")
    private List<@CheckFileSize(maxSizeInMB = 5, message = "{app.file-size-message}") MultipartFile> fileImage;

    @Size(min = 1, max = 200, message = "Название в пределах от 1 до 200 символов")
    private String title;

    @Size(min = 1, max = 200, message = "Описание должено быть в пределах 1 и 200 символов")
    private String description;

    @CheckIsZero(message = "Цена не может быть 0 byn")
    @Min(value = 0, message = "Цена не может быть отрицательной")
    @Max(value = 10000, message = "Цена не должна превышать 10000 byn")
    private Float price;

    private List<ProductImageDto> images;
}
