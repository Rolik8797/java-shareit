package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class BookingCreateRequestDto {
    private LocalDateTime start;

    private LocalDateTime end;

    private Long bookerId;

    private Long itemId;
}