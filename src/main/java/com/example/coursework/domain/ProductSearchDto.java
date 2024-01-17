package com.example.coursework.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductSearchDto {
    private String title;
    private Float price;
}
