package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;


import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.markers.ConstantsUtil.USER_ID_HEADER;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(
            @RequestBody BookingCreateRequestDto requestDto,
            @RequestHeader(USER_ID_HEADER) Long userId) {
        Booking booking = Booking
                .bookingBuilder()
                .start(requestDto.getStart())
                .end(requestDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();

        return BookingMapper.toBookingDto(bookingService.create(booking, requestDto.getItemId(), userId));
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingDto approve(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved) {
        return BookingMapper.toBookingDto(bookingService.approve(bookingId, ownerId, approved));
    }

    @GetMapping(value = "/{bookingId}")
    public BookingDto findById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long bookingId) {
        return BookingMapper.toBookingDto(bookingService.findById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> findByBookerId(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") BookingService.BookingState state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return bookingService
                .findByBookerId(userId, state, from, size)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @GetMapping(value = "/owner")
    public List<BookingDto> findByOwnerId(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingService.BookingState state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return bookingService
                .findByOwnerId(ownerId, state, from, size)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toUnmodifiableList());
    }
}