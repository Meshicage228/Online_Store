package com.example.coursework.controllers.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
