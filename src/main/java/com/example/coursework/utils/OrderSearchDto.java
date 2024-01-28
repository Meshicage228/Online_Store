package com.example.coursework.utils;


import com.example.coursework.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderSearchDto {
    private OrderStatus status;
    private String name;
    private String title;
    private UUID user_id;
}
