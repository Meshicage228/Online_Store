package com.example.coursework.dto.product;

import com.example.coursework.controllers.users.CommentaryDto;
import com.example.coursework.utils.annotations.CheckFileIsEmpty;
import com.example.coursework.utils.annotations.CheckFileSize;
import com.example.coursework.utils.annotations.CheckIsZero;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"fileImage", "images"})
@Data
@Builder
public class ProductDto {
    private Integer id;

    @CheckFileIsEmpty
    @CheckFileSize(maxSizeInMB = 5, message = "{app.file-size-message}")
    private List<MultipartFile> fileImage;

    private List<byte[]> imagesToThrow;

    @NotEmpty(message = "Введите название продукта")
    @Size(min = 1, max = 200, message = "Название в пределах от 1 до 200 символов")
    private String title;

    @NotEmpty(message = "Введите описание продукта")
    @Size(min = 1, max = 200, message = "Описание должено быть в пределах 1 и 200 символов")
    private String description;

    @NotNull(message = "Введите цену продукта")
    @CheckIsZero(message = "Цена не может быть 0 byn")
    @Min(value = 1, message = "Цена не может быть отрицательной")
    @Max(value = 10000, message = "Цена не должна превышать 10000 byn")
    private Float price;

    @NotNull(message = "Введите количество")
    @Min(value = 1, message = "Минимально-допустимое значение : 1")
    @Max(value = 5000, message = "Максимально-допустимое значение : 5000")
    private Integer count;

    private List<ProductImageDto> images;

    private List<CommentaryDto> comments;
}
