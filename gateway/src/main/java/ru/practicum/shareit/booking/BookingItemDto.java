package ru.practicum.shareit.booking;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
public class BookingItemDto {
    Long id;
    Long bookerId;
    LocalDateTime start;
    LocalDateTime end;
}