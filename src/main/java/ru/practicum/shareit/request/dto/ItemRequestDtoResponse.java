package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ItemRequestDtoResponse {
    private Long id;
    private String description;
    private LocalDateTime created;
}