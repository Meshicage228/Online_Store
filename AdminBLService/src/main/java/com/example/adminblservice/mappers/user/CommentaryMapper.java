package com.example.adminblservice.mappers.user;

import com.example.adminblservice.dto.user.CommentaryDto;
import com.example.adminblservice.entity.user.Commentary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.Base64;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CommentaryMapper {
    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", expression = "java(entity.getUser().getName())"),
            @Mapping(target = "userAvatar", expression = "java(getStringAvatar(entity.getUser().getAvatar()))"),
            @Mapping(target = "comment", source = "comment"),
            @Mapping(target = "date", source = "date")
    })
    CommentaryDto toDto(Commentary entity);

    default String getStringAvatar(byte[] avatar){
        return Base64.getEncoder().encodeToString(avatar);
    }
}
