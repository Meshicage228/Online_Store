package com.example.adminblservice.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentaryDto {
    private Integer id;

    private String userAvatar;

    private String name;

    private String comment;
}
