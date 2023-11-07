package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.userBuilder().name("User").email("user@test.ru").build());
        item = itemRepository.save(Item.itemBuilder().name("Item").description("Description").available(true).owner(user).build());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findByBookerIdPast() {
        LocalDateTime now = LocalDateTime.now();
        Booking bookingForSave = Booking
                .bookingBuilder()
                .item(item)
                .booker(user)
                .start(now.minusHours(3))
                .end(now.minusHours(2))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(bookingForSave);
        List<Booking> bookings = bookingRepository.findByBookerIdPast(user.getId(), PageRequest.of(0, 10));
        assertFalse(bookings.isEmpty());
        Booking booking = bookings.get(0);
        assertEquals(bookingForSave.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingForSave.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingForSave.getStart(), booking.getStart());
        assertEquals(bookingForSave.getEnd(), booking.getEnd());
        assertEquals(bookingForSave.getStatus(), booking.getStatus());
    }

    @Test
    void findByBookerIdFuture() {
        LocalDateTime now = LocalDateTime.now();
        Booking bookingForSave = Booking
                .bookingBuilder()
                .item(item)
                .booker(user)
                .start(now.plusHours(2))
                .end(now.plusHours(3))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(bookingForSave);
        List<Booking> bookings = bookingRepository.findByBookerIdFuture(user.getId(), PageRequest.of(0, 10));
        assertFalse(bookings.isEmpty());
        Booking booking = bookings.get(0);
        assertEquals(bookingForSave.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingForSave.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingForSave.getStart(), booking.getStart());
        assertEquals(bookingForSave.getEnd(), booking.getEnd());
        assertEquals(bookingForSave.getStatus(), booking.getStatus());
    }

    @Test
    void findByBookerIdAndStatus() {
        LocalDateTime now = LocalDateTime.now();
        Booking bookingForSave = Booking
                .bookingBuilder()
                .item(item)
                .booker(user)
                .start(now.plusHours(2))
                .end(now.plusHours(3))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(bookingForSave);
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(user.getId(), BookingStatus.APPROVED, PageRequest.of(0, 10));
        assertFalse(bookings.isEmpty());
        Booking booking = bookings.get(0);
        assertEquals(bookingForSave.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingForSave.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingForSave.getStart(), booking.getStart());
        assertEquals(bookingForSave.getEnd(), booking.getEnd());
        assertEquals(bookingForSave.getStatus(), booking.getStatus());
    }

    @Test
    void findByOwnerIdCurrent() {
        LocalDateTime now = LocalDateTime.now();
        Booking bookingForSave = Booking
                .bookingBuilder()
                .item(item)
                .booker(user)
                .start(now.minusHours(2))
                .end(now.plusHours(3))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(bookingForSave);
        List<Booking> bookings = bookingRepository.findByOwnerIdCurrent(user.getId(), PageRequest.of(0, 10));
        assertFalse(bookings.isEmpty());
        Booking booking = bookings.get(0);
        assertEquals(bookingForSave.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingForSave.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingForSave.getStart(), booking.getStart());
        assertEquals(bookingForSave.getEnd(), booking.getEnd());
        assertEquals(bookingForSave.getStatus(), booking.getStatus());
    }

    @Test
    void findByOwnerIdPast() {
        LocalDateTime now = LocalDateTime.now();
        Booking bookingForSave = Booking
                .bookingBuilder()
                .item(item)
                .booker(user)
                .start(now.minusHours(3))
                .end(now.minusHours(2))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(bookingForSave);
        List<Booking> bookings = bookingRepository.findByOwnerIdPast(user.getId(), PageRequest.of(0, 10));
        assertFalse(bookings.isEmpty());
        Booking booking = bookings.get(0);
        assertEquals(bookingForSave.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingForSave.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingForSave.getStart(), booking.getStart());
        assertEquals(bookingForSave.getEnd(), booking.getEnd());
        assertEquals(bookingForSave.getStatus(), booking.getStatus());
    }

    @Test
    void findByOwnerIdFuture() {
        LocalDateTime now = LocalDateTime.now();
        Booking bookingForSave = Booking
                .bookingBuilder()
                .item(item)
                .booker(user)
                .start(now.plusHours(3))
                .end(now.plusHours(4))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(bookingForSave);
        List<Booking> bookings = bookingRepository.findByOwnerIdFuture(user.getId(), PageRequest.of(0, 10));
        assertFalse(bookings.isEmpty());
        Booking booking = bookings.get(0);
        assertEquals(bookingForSave.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingForSave.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingForSave.getStart(), booking.getStart());
        assertEquals(bookingForSave.getEnd(), booking.getEnd());
        assertEquals(bookingForSave.getStatus(), booking.getStatus());
    }

    @Test
    void findByOwnerIdAndStatus() {
        LocalDateTime now = LocalDateTime.now();
        Booking bookingForSave = Booking
                .bookingBuilder()
                .item(item)
                .booker(user)
                .start(now.minusHours(3))
                .end(now.minusHours(2))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(bookingForSave);
        List<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(user.getId(), BookingStatus.APPROVED, PageRequest.of(0, 10));
        assertFalse(bookings.isEmpty());
        Booking booking = bookings.get(0);
        assertEquals(bookingForSave.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingForSave.getBooker().getId(), booking.getBooker().getId());
        assertEquals(bookingForSave.getStart(), booking.getStart());
        assertEquals(bookingForSave.getEnd(), booking.getEnd());
        assertEquals(bookingForSave.getStatus(), booking.getStatus());
    }
}