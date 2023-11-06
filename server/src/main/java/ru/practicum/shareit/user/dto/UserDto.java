package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@ToString
public class UserDto {
  private Long id;
  private String name;
   private String email;
}