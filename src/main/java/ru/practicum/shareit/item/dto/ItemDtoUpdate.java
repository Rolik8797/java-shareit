package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
public class ItemDtoUpdate {
    @Pattern(regexp = "^[^ ].*[^ ]$", message = "Некорректное имя")
    @Size(max = 255)
    private String name;
    @Pattern(regexp = "^[^ ].*[^ ]$", message = "Некорректное описание")
    @Size(max = 500)
    private String description;
    private Boolean available;
}