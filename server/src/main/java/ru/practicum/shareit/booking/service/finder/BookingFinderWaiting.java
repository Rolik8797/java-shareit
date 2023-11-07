package ru.practicum.shareit.booking.service.finder;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingFinderWaiting implements BookingFinder {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> findByOwnerId(Long ownerId, Pageable pageable) {
        return bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable);
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId, Pageable pageable) {
        return bookingRepository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageable);
    }

    @Override
    public BookingService.BookingState getSearchType() {
        return BookingService.BookingState.WAITING;
    }
}