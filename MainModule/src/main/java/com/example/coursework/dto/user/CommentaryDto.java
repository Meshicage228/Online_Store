package com.example.coursework.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentaryDto {
    private Integer id;

    private String userAvatar;

    private String name;

    private String comment;

    private Date date;
}
