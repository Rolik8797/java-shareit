package ru.practicum.shareit.request.dto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class ItemRequestDto {
  private   Long id;
  private String description;
  private Long requestor;
  private LocalDateTime created;
}