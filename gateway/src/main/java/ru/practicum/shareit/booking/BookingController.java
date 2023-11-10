package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.ConstantsUtil.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody BookingCreateRequestDto requestDto,
            @NotNull @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Create booking");
        return bookingClient.create(requestDto, userId);
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<Object> approve(
            @NotNull @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("Approve booking {}", bookingId);
        return bookingClient.approve(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable Long bookingId
    ) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findByBookerId(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.findByBookerId(userId, state, from, size);
    }

    @GetMapping(value = "/owner")
    public ResponseEntity<Object> findByOwnerId(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.findByOwnerId(userId, state, from, size);
    }
}