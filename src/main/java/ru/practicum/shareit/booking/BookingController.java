package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.AccessLevel;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.util.UriBuilderUtil;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    private final UriBuilderUtil uriBuilderUtil;

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Valid @RequestBody BookingInputDto bookingInputDto) {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/bookings");
        Logger.logRequest(HttpMethod.POST, uriComponents.toUriString(), bookingInputDto.toString());
        return ResponseEntity.status(201).body(bookingService.addBooking(userId, bookingInputDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveOrRejectBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        UriComponents uriComponents = uriBuilderUtil.buildUriWithQueryParams("/bookings/{bookingId}", "approved", approved);
        Logger.logRequest(HttpMethod.PATCH, uriComponents.toUriString(), "no body");
        return ResponseEntity.ok().body(bookingService.approveOrRejectBooking(userId, bookingId, approved, AccessLevel.OWNER));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/bookings/{bookingId}");
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), "no body");
        return ResponseEntity.ok().body(bookingService.getBooking(bookingId, userId, AccessLevel.OWNER_AND_BOOKER));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookingsOfCurrentUser(@RequestParam(defaultValue = "ALL") String state,
                                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        UriComponents uriComponents = uriBuilderUtil.buildUriWithQueryParams("/bookings/", "state", state);
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), "no body");
        return ResponseEntity.ok().body(bookingService.getBookingsOfCurrentUser(State.convert(state), userId));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        UriComponents uriComponents = uriBuilderUtil.buildUriWithQueryParams("/bookings/owner", "state", state);
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), "no body");
        return ResponseEntity.ok().body(bookingService.getBookingsOfOwner(State.convert(state), userId));
    }
}