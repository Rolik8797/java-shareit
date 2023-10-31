package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

import javax.validation.constraints.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@ToString
public class UserDto {
    Long id;
    String name;
    @NotBlank(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    String email;
}