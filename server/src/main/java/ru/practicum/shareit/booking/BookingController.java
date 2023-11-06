package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.markers.Constants;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public BookingResponseDto getById(@RequestHeader(Constants.headerUserId) Long userId,
                                      @PathVariable Long id) {
        log.info("Получен запрос GET /bookings/id  запрос на вещь с id" + id);
        return bookingService.getById(userId, id);
    }

    @GetMapping
    public List<BookingResponseDto> getAllByBookerId(
            @RequestHeader(Constants.headerUserId) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM)  Integer from,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) Integer size) {
        State stateEnum = State.stringToState(state).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Получен запрос всех вещей бронирующего GET /bookings " + userId);
        return bookingService.getAllByBookerId(userId, stateEnum, PageRequest.of(from / size, size));
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllByOwnerId(
            @RequestHeader(Constants.headerUserId) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM)  Integer from,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) Integer size) {
        State stateEnum = State.stringToState(state).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Получен запрос всех вещей владельца GET /bookings/owner " + userId);
        return bookingService.getAllByOwnerId(userId, stateEnum, PageRequest.of(from / size, size));
    }

    @PostMapping
    public BookingResponseDto add(@RequestHeader(Constants.headerUserId) Long userId,
                                 @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Получен запрос POST /bookings " + userId);
        return bookingService.add(userId, bookingRequestDto);
    }

    @PatchMapping("/{id}")
    public BookingResponseDto update(@RequestHeader(Constants.headerUserId) Long userId,
                                     @PathVariable Long id,
                                     @RequestParam() Boolean approved) {
        log.info("Получен запрос PATCH /bookings/id " + " ! статус брони вещи с id" + id + ": забронировано=" + approved + " юзер с id" + userId);
        return bookingService.update(userId, id, approved);
    }
}