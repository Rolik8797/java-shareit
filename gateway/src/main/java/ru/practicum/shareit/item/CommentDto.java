package ru.practicum.shareit.item;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentDto {
    @NotBlank
    private String text;

}