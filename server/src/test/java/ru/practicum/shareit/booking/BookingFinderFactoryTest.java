package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.finder.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingFinderFactoryTest {
    @Mock
    private BookingRepository bookingRepository;
    private final BookingFinderAll bookingFinderAll = new BookingFinderAll(bookingRepository);
    private final BookingFinderCurrent bookingFinderCurrent = new BookingFinderCurrent(bookingRepository);
    private final BookingFinderFuture bookingFinderFuture = new BookingFinderFuture(bookingRepository);
    private final BookingFinderPast bookingFinderPast = new BookingFinderPast(bookingRepository);
    private final BookingFinderRejected bookingFinderRejected = new BookingFinderRejected(bookingRepository);
    private final BookingFinderWaiting bookingFinderWaiting = new BookingFinderWaiting(bookingRepository);
    private final BookingFinderFactory bookingFinderFactory = new BookingFinderFactory(Set.of(
            bookingFinderAll,
            bookingFinderCurrent,
            bookingFinderFuture,
            bookingFinderPast,
            bookingFinderRejected,
            bookingFinderWaiting
    ));

    @Test
    void shouldGetFinder() {
        assertTrue(bookingFinderFactory.getFinder(BookingService.BookingState.ALL) instanceof BookingFinderAll);
        assertTrue(bookingFinderFactory.getFinder(BookingService.BookingState.CURRENT) instanceof BookingFinderCurrent);
        assertTrue(bookingFinderFactory.getFinder(BookingService.BookingState.FUTURE) instanceof BookingFinderFuture);
        assertTrue(bookingFinderFactory.getFinder(BookingService.BookingState.PAST) instanceof BookingFinderPast);
        assertTrue(bookingFinderFactory.getFinder(BookingService.BookingState.REJECTED) instanceof BookingFinderRejected);
        assertTrue(bookingFinderFactory.getFinder(BookingService.BookingState.WAITING) instanceof BookingFinderWaiting);
    }
}