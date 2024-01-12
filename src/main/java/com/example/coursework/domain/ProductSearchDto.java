package com.example.coursework.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)

@Builder
@Getter
@Setter
public class ProductSearchDto {
    String title;
    Float price;
}
