package ru.practicum.shareit.booking.service.finder;

import org.springframework.data.domain.Pageable;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


public interface BookingFinder {
    List<Booking> findByOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    BookingService.BookingState getSearchType();
}