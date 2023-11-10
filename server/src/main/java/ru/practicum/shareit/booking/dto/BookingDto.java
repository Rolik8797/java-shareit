package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder(builderMethodName = "bookingDtoBuilder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDtoForBooking item;

    private UserDto booker;

    private BookingStatus status;

    @Getter
    @Builder
    public static class ItemDtoForBooking {
        private Long id;
        private String name;
    }
}