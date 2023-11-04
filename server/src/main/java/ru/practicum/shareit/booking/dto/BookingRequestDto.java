package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@Builder
@Setter
@Getter
@Validated
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingRequestDto {

    private final Long itemId;

    @NotNull
    private final LocalDateTime start;

    @NotNull
    private final LocalDateTime end;
}

