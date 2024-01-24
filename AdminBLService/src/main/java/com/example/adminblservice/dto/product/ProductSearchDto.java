package com.example.adminblservice.dto.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductSearchDto {
    String title;
    Float price;
    String sorting;
}
