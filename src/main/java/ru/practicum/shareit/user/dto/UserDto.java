package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {
    @Pattern(regexp = "^[^ ].*[^ ]$", message = "Некорректное имя")
    @Size(max = 255)
    @NotNull
    private String name;
    @Email(message = "Некорректный email")
    @NotEmpty(message = "Поле email обязательно")
    private String email;
}