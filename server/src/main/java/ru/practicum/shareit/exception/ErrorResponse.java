package ru.practicum.shareit.exception;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ErrorResponse {
    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();
    int status;
    String error;
}