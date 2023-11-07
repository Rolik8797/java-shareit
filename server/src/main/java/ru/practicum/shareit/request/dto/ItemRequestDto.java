package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(builderClassName = "itemRequestDtoBuilder")
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequestDto {
  private Long id;

  private String description;

  private UserDto requester;

  private LocalDateTime created;

  private List<Item> items;

  @Getter
  @Builder
  public static class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
  }
}