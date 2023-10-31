package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingItemDto {
    Long id;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
}