package com.example.coursework.dto.product;

import com.example.coursework.dto.user.CommentaryDto;
import com.example.coursework.utils.annotations.CheckFileIsEmpty;
import com.example.coursework.utils.annotations.CheckFileSize;
import com.example.coursework.utils.annotations.CheckIsZero;
import com.example.coursework.utils.markers.OnCreateMarker;
import com.example.coursework.utils.markers.OnUpdateMarker;
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
    private Integer cart_id;
    private Float bill;

    @CheckFileIsEmpty(groups = OnCreateMarker.class)
    @NotNull(groups = OnCreateMarker.class)
    @CheckFileSize(maxSizeInMB = 5, message = "{app.file-size-message}", groups = OnCreateMarker.class)
    private List<MultipartFile> fileImage;

    private List<byte[]> imagesToThrow;

    @NotEmpty(message = "Введите название продукта", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    @Size(min = 1, max = 200, message = "Название в пределах от 1 до 200 символов", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    private String title;

    @NotEmpty(message = "Введите описание продукта", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    @Size(min = 1, max = 200, message = "Описание должено быть в пределах 1 и 200 символов", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    private String description;

    @NotNull(message = "Введите цену продукта", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    @CheckIsZero(message = "Цена не может быть 0 byn", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    @Min(value = 1, message = "Цена не может быть отрицательной", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    @Max(value = 10000, message = "Цена не должна превышать 10000 byn", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    private Float price;

    @NotNull(message = "Введите количество", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    @Min(value = 1, message = "Минимально-допустимое значение : 1", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    @Max(value = 5000, message = "Максимально-допустимое значение : 5000", groups = {OnCreateMarker.class, OnUpdateMarker.class})
    private Integer count;

    private List<ProductImageDto> images;

    private List<CommentaryDto> comments;
}
