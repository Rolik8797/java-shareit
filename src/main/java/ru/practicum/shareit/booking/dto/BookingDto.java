package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;


import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    @NotNull(message = "Поле start обязательно")
    @FutureOrPresent(message = "Начало бронирования не может быть в прошлом")
    private LocalDateTime start;
    @Future(message = "Конец бронирования не может быть в прошлом")
    @NotNull(message = "Поле end обязательно")
    private LocalDateTime end;
    @NotNull(message = "Поле itemId обязательно")
    @Min(value = 1, message = "Некорректный itemId")
    private Long itemId;
    private Status status;
}