package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.finder.BookingFinder;
import ru.practicum.shareit.booking.service.finder.BookingFinderFactory;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingFinder bookingFinder;
    @Mock
    private BookingFinderFactory bookingFinderFactory;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User booker = User
            .userBuilder()
            .id(1L)
            .name("User")
            .email("user@test.com")
            .build();

    private final User owner = User
            .userBuilder()
            .id(2L)
            .name("owner")
            .email("owner@test.com")
            .build();

    private final Item item = Item
            .itemBuilder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .owner(owner)
            .build();

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = Booking
                .bookingBuilder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void shouldCreate() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(userRepository.findById(99L)).thenThrow(UserNotFoundException.class);
        when(itemRepository.isBookedInPeriod(any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any())).thenReturn(booking);
        item.setAvailable(false);
        assertThrows(ItemUnavailableException.class, () -> bookingService.create(booking, 1L, 1L));
        item.setAvailable(true);
        assertThrows(UserNotFoundException.class, () -> bookingService.create(booking, 1L, 99L));
        assertEquals(booking, bookingService.create(booking, 1L, 1L));
        when(itemRepository.isBookedInPeriod(any(), any(), any())).thenReturn(true);
        assertThrows(ItemNotFoundException.class, () -> bookingService.create(booking, 1L, 1L));
    }

    @Test
    void shouldApprove() {
        when(bookingRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        assertEquals(BookingStatus.APPROVED, bookingService.approve(1L, 1L, true).getStatus());
        assertThrows(BookingIsAlreadyApprovedException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    void shouldFindById() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());
        assertEquals(booking, bookingService.findById(1L, 1L));
        assertThrows(BookingNotFoundException.class, () -> bookingService.findById(99L, 1L));
        booking.setBooker(owner);
        assertThrows(BookingNotFoundException.class, () -> bookingService.findById(1L, 1L));
    }

    @Test
    void shouldFindByBookerId() {
        List<Booking> bookings = List.of(booking);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingFinderFactory.getFinder(any())).thenReturn(bookingFinder);
        when(bookingFinder.findByBookerId(any(), any())).thenReturn(bookings);
        assertEquals(bookings, bookingService.findByBookerId(1L, BookingService.BookingState.ALL, 0, 10));
        assertEquals(bookings, bookingService.findByBookerId(1L, BookingService.BookingState.CURRENT, 0, 10));
        assertEquals(bookings, bookingService.findByBookerId(1L, BookingService.BookingState.FUTURE, 0, 10));
        assertEquals(bookings, bookingService.findByBookerId(1L, BookingService.BookingState.WAITING, 0, 10));
        assertEquals(bookings, bookingService.findByBookerId(1L, BookingService.BookingState.PAST, 0, 10));
        assertEquals(bookings, bookingService.findByBookerId(1L, BookingService.BookingState.REJECTED, 0, 10));
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> bookingService.findByBookerId(99L, BookingService.BookingState.ALL, 0, 10));
    }

    @Test
    void shouldFindByOwnerId() {
        List<Booking> bookings = List.of(booking);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingFinderFactory.getFinder(any())).thenReturn(bookingFinder);
        when(bookingFinder.findByOwnerId(any(), any())).thenReturn(bookings);
        assertEquals(bookings, bookingService.findByOwnerId(2L, BookingService.BookingState.ALL, 0, 10));
        assertEquals(bookings, bookingService.findByOwnerId(2L, BookingService.BookingState.CURRENT, 0, 10));
        assertEquals(bookings, bookingService.findByOwnerId(2L, BookingService.BookingState.FUTURE, 0, 10));
        assertEquals(bookings, bookingService.findByOwnerId(2L, BookingService.BookingState.WAITING, 0, 10));
        assertEquals(bookings, bookingService.findByOwnerId(2L, BookingService.BookingState.PAST, 0, 10));
        assertEquals(bookings, bookingService.findByOwnerId(2L, BookingService.BookingState.REJECTED, 0, 10));
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> bookingService.findByOwnerId(99L, BookingService.BookingState.ALL, 0, 10));
    }
}