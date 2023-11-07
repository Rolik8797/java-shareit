package ru.practicum.shareit.booking.service.finder;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@Component
@RequiredArgsConstructor
public class BookingFinderCurrent implements BookingFinder {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findByOwnerId(Long ownerId, Pageable pageable) {
        return bookingRepository.findByOwnerIdCurrent(ownerId, pageable);
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId, Pageable pageable) {
        return bookingRepository.findByBookerIdCurrent(bookerId, pageable);
    }

    @Override
    public BookingService.BookingState getSearchType() {
        return BookingService.BookingState.CURRENT;
    }
}