package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import ru.practicum.shareit.booking.dto.BookingListDto;
import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class BookingServiceTest extends Bookings {
    @Mock
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private BookingDto booking1Dto;

    @BeforeEach
    public void setUp() {

        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");
        user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");
        item1 = new Item();
        item1.setName("test item");
        item1.setDescription("test item description");
        item1.setAvailable(Boolean.TRUE);
        item1.setOwner(user1);
        item2 = new Item();
        item2.setName("test item2");
        item2.setDescription("test item2 description");
        item2.setAvailable(Boolean.TRUE);
        item2.setOwner(user2);
        booking1Dto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();

        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void createAndGetBooking() {

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);
        when(itemRepository.save(item1)).thenReturn(item1);

        BookingDtoResponse createdBookingDto = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .item(ItemShortDto.builder().id(1L).name("Sample Item").build())
                .booker(UserShortDto.builder().id(1L).name("John Doe").build())
                .status(Status.APPROVED)
                .build();

        when(bookingService.createBooking(eq(user2.getId()), any(BookingDto.class)))
                .thenReturn(createdBookingDto);

        when(bookingService.getBookingByIdForOwnerAndBooker(eq(createdBookingDto.getId()), eq(user2.getId())))
                .thenReturn(createdBookingDto);

        BookingDtoResponse savedBookingDto = bookingService.createBooking(user2.getId(), booking1Dto);
        BookingDtoResponse findBookingDto = bookingService.getBookingByIdForOwnerAndBooker(savedBookingDto.getId(), user2.getId());

        assertThat(savedBookingDto).usingRecursiveComparison().ignoringFields("start", "end")
                .isEqualTo(findBookingDto);
    }

    @Test
    public void createBookingWhenEndBeforeStart() {

        booking1Dto.setStart(LocalDateTime.now().plusDays(2));
        booking1Dto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);
        when(itemRepository.save(item1)).thenReturn(item1);

        try {
            bookingService.createBooking(user2.getId(), booking1Dto);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getReason()).contains("End time cannot be before start time");
        }
    }

    @Test
    public void createBookingWithNotExistingItem() {

        booking1Dto.setItemId(2L);

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);
        when(itemRepository.save(item1)).thenReturn(item1);

        when(itemRepository.findById(booking1Dto.getItemId())).thenReturn(Optional.empty());

        try {
            bookingService.createBooking(user2.getId(), booking1Dto);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(e.getReason()).contains("Item not found");
        }
    }

    @Test
    public void createBookingWhenBookerIsOwner() {

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        try {
            bookingService.createBooking(user1.getId(), booking1Dto);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(e.getReason()).contains("Cannot book your own item");
        }
    }

    @Test
    public void createBookingWhenNotExistingBooker() {

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        try {
            bookingService.createBooking(99L, booking1Dto);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(e.getReason()).contains("Booker not found");
        }
    }

    @Test
    public void createBookingWithNotAvailableItem() {

        item1.setAvailable(false);

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        try {
            bookingService.createBooking(user2.getId(), booking1Dto);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(e.getReason()).contains("Item is not available");
        }
    }

    @Test
    public void approveBooking() {

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);
        when(itemRepository.save(item1)).thenReturn(item1);

        BookingDtoResponse savedBooking = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(ItemShortDto.builder().id(1L).name("Sample Item").build())
                .booker(UserShortDto.builder().id(1L).name("John Doe").build())
                .status(Status.WAITING)
                .build();

        BookingDtoResponse approvedBooking = bookingService.approveBooking(user1.getId(), savedBooking.getId(), true);
        BookingDtoResponse findBooking = bookingService.getBookingByIdForOwnerAndBooker(savedBooking.getId(), user2.getId());

        assertThat(approvedBooking).usingRecursiveComparison().isEqualTo(findBooking);
    }

    @Test
    public void rejectBooking() {

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);
        when(itemRepository.save(item1)).thenReturn(item1);

        BookingDtoResponse savedBooking = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(ItemShortDto.builder().id(1L).name("Sample Item").build())
                .booker(UserShortDto.builder().id(1L).name("John Doe").build())
                .status(Status.WAITING)
                .build();

        BookingDtoResponse rejectedBooking = bookingService.approveBooking(user1.getId(), savedBooking.getId(), false);
        BookingDtoResponse findBooking = bookingService.getBookingByIdForOwnerAndBooker(savedBooking.getId(), user2.getId());

        assertThat(rejectedBooking).usingRecursiveComparison().isEqualTo(findBooking);
    }

    @Test
    public void approveBookingWithNotExistingBooking() {

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        try {
            bookingService.approveBooking(user1.getId(), 99L, true);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(e.getReason()).contains("Booking not found");
        }
    }

    @Test
    public void approveBookingWhenBookingIsNotWaiting() {

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);
        when(itemRepository.save(item1)).thenReturn(item1);

        BookingDtoResponse savedBooking = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(ItemShortDto.builder().id(1L).name("Sample Item").build())
                .booker(UserShortDto.builder().id(1L).name("John Doe").build())
                .status(Status.WAITING)
                .build();

        bookingService.approveBooking(user1.getId(), savedBooking.getId(), false);

        try {
            bookingService.approveBooking(user1.getId(), savedBooking.getId(), true);
        } catch (ResponseStatusException ex) {
            assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(ex.getReason()).isEqualTo("Booking is already approved");
        }
    }

    @Test
    public void approveBookingWhenUserIsNotOwner() {

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        var savedBooking = bookingService.createBooking(user2.getId(), booking1Dto);
        assertThatThrownBy(

                () -> bookingService.approveBooking(user2.getId(), savedBooking.getId(), true)

        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void getBookingWhenBookingNotFound() {

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);

        bookingService.createBooking(user2.getId(), booking1Dto);

        try {
            bookingService.getBookingByIdForOwnerAndBooker(99L, user2.getId());
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getReason()).isEqualTo("Booking not found");
        }
    }

    @Test
    public void getBookingWhenUserIsNotOwnerOrBooker() {

        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        var savedBooking = bookingService.createBooking(user2.getId(), booking1Dto);
        assertThatThrownBy(

                () -> bookingService.getBookingByIdForOwnerAndBooker(savedBooking.getId(), 10L)

        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void getAllBookingForUserWhenStateIsAll() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        BookingListDto findBookingList = bookingService.getAllBookingsForUser(PageRequest.of(0, 10), user2.getId(), "ALL");

        if (findBookingList != null && findBookingList.getBookings() != null) {
            assertThat(findBookingList.getBookings().size()).isEqualTo(10);

            List<Long> bookingIds = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());

            List<Long> expectedIds = Arrays.asList(
                    futureBookingForItem2.getId(),
                    futureBookingForItem1.getId(),
                    rejectedBookingForItem2.getId(),
                    rejectedBookingForItem1.getId(),
                    waitingBookingForItem2.getId(),
                    waitingBookingForItem1.getId(),
                    currentBookingForItem2.getId(),
                    currentBookingForItem1.getId(),
                    pastBookingForItem2.getId(),
                    pastBookingForItem1.getId()
            );

            assertThat(bookingIds).containsExactlyElementsOf(expectedIds);

            assertThat(item1.equals(item2)).isFalse();
        }
    }

    @Test
    public void getAllBookingsForItemsUser() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        BookingListDto findBookingList = bookingService.getAllBookingsForItemsUser(PageRequest.of(0, 10), user1.getId(), "ALL");

        if (findBookingList != null && findBookingList.getBookings() != null) {
            assertThat(findBookingList.getBookings().size()).isEqualTo(5);

            List<Long> bookingIds = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());

            List<Long> expectedIds = Arrays.asList(
                    futureBookingForItem1.getId(),
                    rejectedBookingForItem1.getId(),
                    waitingBookingForItem1.getId(),
                    currentBookingForItem1.getId(),
                    pastBookingForItem1.getId()
            );

            assertThat(bookingIds).containsExactlyElementsOf(expectedIds);
        }
    }

    @Test
    public void getAllBookingsForUserWhenStateIsCurrent() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        BookingListDto findBookingList = bookingService.getAllBookingsForUser(PageRequest.of(0, 10), user2.getId(), "CURRENT");

        if (findBookingList != null && findBookingList.getBookings() != null) {
            assertThat(findBookingList.getBookings().size()).isEqualTo(2);

            List<Long> bookingIds = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());

            List<Long> expectedIds = Arrays.asList(currentBookingForItem2.getId(), currentBookingForItem1.getId());

            assertThat(bookingIds).containsExactlyElementsOf(expectedIds);
        }
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsCurrent() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        BookingListDto findBookingList = bookingService.getAllBookingsForItemsUser(PageRequest.of(0, 10), user1.getId(), "CURRENT");

        if (findBookingList != null && findBookingList.getBookings() != null) {

            List<Long> bookingIds = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());

            assertThat(bookingIds).singleElement().isEqualTo(currentBookingForItem1.getId());
        }
    }

    @Test
    public void getAllBookingsForUserWhenStateIsPast() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        BookingListDto findBookingList = bookingService.getAllBookingsForUser(PageRequest.of(0, 10), user2.getId(), "PAST");

        if (findBookingList != null && findBookingList.getBookings() != null) {

            List<Long> bookingIds = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());

            assertThat(bookingIds.size()).isEqualTo(2);
            assertThat(bookingIds).first().isEqualTo(pastBookingForItem2.getId());
            assertThat(bookingIds).last().isEqualTo(pastBookingForItem1.getId());
        }
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsPast() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForItemsUser(PageRequest.of(0, 10), user1.getId(), "PAST");

        if (findBookingList != null && findBookingList.getBookings() != null) {
            List<Long> ids = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());
            assertThat(ids).singleElement().isEqualTo(pastBookingForItem1.getId());
        }
    }

    @Test
    public void getAllBookingsForUserWhenStateIsFuture() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForUser(PageRequest.of(0, 10), user2.getId(), "Future");

        if (findBookingList != null && findBookingList.getBookings() != null) {
            assertThat(findBookingList.getBookings().size()).isEqualTo(6);
            List<Long> ids = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());
            assertThat(ids).first().isEqualTo(futureBookingForItem2.getId());
            assertThat(ids).element(1).isEqualTo(futureBookingForItem1.getId());
            assertThat(ids).element(2).isEqualTo(rejectedBookingForItem2.getId());
            assertThat(ids).element(3).isEqualTo(rejectedBookingForItem1.getId());
            assertThat(ids).element(4).isEqualTo(waitingBookingForItem2.getId());
            assertThat(ids).element(5).isEqualTo(waitingBookingForItem1.getId());
        }
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsFuture() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForItemsUser(PageRequest.of(0, 10), user1.getId(), "Future");

        if (findBookingList != null && findBookingList.getBookings() != null) {
            assertThat(findBookingList.getBookings().size()).isEqualTo(3);
            List<Long> ids = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());
            assertThat(ids).first().isEqualTo(futureBookingForItem1.getId());
            assertThat(ids).element(1).isEqualTo(rejectedBookingForItem1.getId());
            assertThat(ids).element(2).isEqualTo(waitingBookingForItem1.getId());
        }
    }

    @Test
    public void getAllBookingsForUserWhenStateIsWaiting() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForUser(PageRequest.of(0, 10), user2.getId(), "waiting");

        if (findBookingList != null && findBookingList.getBookings() != null) {
            assertThat(findBookingList.getBookings().size()).isEqualTo(2);
            List<Long> ids = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());
            assertThat(ids).first().isEqualTo(waitingBookingForItem2.getId());
            assertThat(ids).last().isEqualTo(waitingBookingForItem1.getId());
        }
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsWaiting() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForItemsUser(PageRequest.of(0, 10), user1.getId(), "waiting");

        if (findBookingList != null && findBookingList.getBookings() != null) {
            List<Long> ids = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());
            assertThat(ids).singleElement().isEqualTo(waitingBookingForItem1.getId());
        }
    }

    @Test
    public void getAllBookingsForUserWhenStateIsRejected() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForUser(PageRequest.of(0, 10), user2.getId(), "rejected");

        if (findBookingList != null && findBookingList.getBookings() != null) {
            List<Long> ids = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());
            assertThat(ids).first().isEqualTo(rejectedBookingForItem2.getId());
            assertThat(ids).last().isEqualTo(rejectedBookingForItem1.getId());
        }
    }

    @Test
    public void getAllBookingsForItemsUserWhenStateIsRejected() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        var findBookingList = bookingService
                .getAllBookingsForItemsUser(PageRequest.of(0, 10), user1.getId(), "rejected");

        if (findBookingList != null && findBookingList.getBookings() != null) {
            List<Long> ids = findBookingList.getBookings().stream()
                    .map(BookingDtoResponse::getId)
                    .collect(Collectors.toList());
            assertThat(ids).singleElement().isEqualTo(rejectedBookingForItem1.getId());
        }
    }

    @Test
    public void getBookingListWithUnknownState() {

        userRepository.save(user1);

        try {
            bookingService.getAllBookingsForUser(PageRequest.of(0, 10), user1.getId(), "qwe");

        } catch (StateException e) {
            assertThat(e).isInstanceOf(StateException.class);
            assertThat(e.getMessage()).isEqualTo("Your expected error message");
        }
    }

    @Test
    public void getAllBookingsForUserWhenUserNotFound() {

        userRepository.save(user1);

        try {
            bookingService.getAllBookingsForUser(PageRequest.of(0, 10), 50L, "ALL");

        } catch (ResponseStatusException e) {
            assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getReason()).isEqualTo("User not found");
        }
    }

    @Test
    public void getAllBookingsForItemsUserWhenUserNotFound() {

        initializationItem2AndBookings();
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        addBookingsInDb();

        try {
            bookingService.getAllBookingsForItemsUser(PageRequest.of(0, 10), 50L, "ALL");

        } catch (ResponseStatusException e) {
            assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getReason()).isEqualTo("User not found");
        }
    }

    @Test
    public void getAllBookingsForItemsUserWhenUserNotExistingBooking() {

        userRepository.save(user1);

        try {
            bookingService.getAllBookingsForItemsUser(PageRequest.of(0, 10), user1.getId(), "ALL");

        } catch (ResponseStatusException e) {
            assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getReason()).isEqualTo("User not found");
        }
    }

    @SneakyThrows
    private void initializationItem2AndBookings() {

        currentBookingForItem1 = new Booking();
        currentBookingForItem1.setStart(LocalDateTime.now().minusDays(1));
        currentBookingForItem1.setEnd(LocalDateTime.now().plusDays(1));
        currentBookingForItem1.setItem(item1);
        currentBookingForItem1.setBooker(user2);
        currentBookingForItem1.setStatus(Status.APPROVED);

        Thread.sleep(50);

        currentBookingForItem2 = new Booking();
        currentBookingForItem2.setStart(LocalDateTime.now().minusDays(1));
        currentBookingForItem2.setEnd(LocalDateTime.now().plusDays(1));
        currentBookingForItem2.setItem(item2);
        currentBookingForItem2.setBooker(user2);
        currentBookingForItem2.setStatus(Status.APPROVED);

        Thread.sleep(50);

        pastBookingForItem1 = new Booking();
        pastBookingForItem1.setStart(LocalDateTime.now().minusDays(2));
        pastBookingForItem1.setEnd(LocalDateTime.now().minusDays(1));
        pastBookingForItem1.setItem(item1);
        pastBookingForItem1.setBooker(user2);
        pastBookingForItem1.setStatus(Status.APPROVED);

        Thread.sleep(50);

        pastBookingForItem2 = new Booking();
        pastBookingForItem2.setStart(LocalDateTime.now().minusDays(2));
        pastBookingForItem2.setEnd(LocalDateTime.now().minusDays(1));
        pastBookingForItem2.setItem(item2);
        pastBookingForItem2.setBooker(user2);
        pastBookingForItem2.setStatus(Status.APPROVED);

        Thread.sleep(50);

        futureBookingForItem1 = new Booking();
        futureBookingForItem1.setStart(LocalDateTime.now().plusDays(1));
        futureBookingForItem1.setEnd(LocalDateTime.now().plusDays(2));
        futureBookingForItem1.setItem(item1);
        futureBookingForItem1.setBooker(user2);
        futureBookingForItem1.setStatus(Status.APPROVED);

        Thread.sleep(50);

        futureBookingForItem2 = new Booking();
        futureBookingForItem2.setStart(LocalDateTime.now().plusDays(1));
        futureBookingForItem2.setEnd(LocalDateTime.now().plusDays(2));
        futureBookingForItem2.setItem(item2);
        futureBookingForItem2.setBooker(user2);
        futureBookingForItem2.setStatus(Status.APPROVED);

        Thread.sleep(50);

        waitingBookingForItem1 = new Booking();
        waitingBookingForItem1.setStart(LocalDateTime.now().plusHours(1));
        waitingBookingForItem1.setEnd(LocalDateTime.now().plusHours(2));
        waitingBookingForItem1.setItem(item1);
        waitingBookingForItem1.setBooker(user2);
        waitingBookingForItem1.setStatus(Status.WAITING);

        Thread.sleep(50);

        waitingBookingForItem2 = new Booking();
        waitingBookingForItem2.setStart(LocalDateTime.now().plusHours(1));
        waitingBookingForItem2.setEnd(LocalDateTime.now().plusHours(2));
        waitingBookingForItem2.setItem(item2);
        waitingBookingForItem2.setBooker(user2);
        waitingBookingForItem2.setStatus(Status.WAITING);

        Thread.sleep(50);

        rejectedBookingForItem1 = new Booking();
        rejectedBookingForItem1.setStart(LocalDateTime.now().plusHours(1));
        rejectedBookingForItem1.setEnd(LocalDateTime.now().plusHours(2));
        rejectedBookingForItem1.setItem(item1);
        rejectedBookingForItem1.setBooker(user2);
        rejectedBookingForItem1.setStatus(Status.REJECTED);

        Thread.sleep(50);

        rejectedBookingForItem2 = new Booking();
        rejectedBookingForItem2.setStart(LocalDateTime.now().plusHours(1));
        rejectedBookingForItem2.setEnd(LocalDateTime.now().plusHours(2));
        rejectedBookingForItem2.setItem(item2);
        rejectedBookingForItem2.setBooker(user2);
        rejectedBookingForItem2.setStatus(Status.REJECTED);
    }

    @SneakyThrows
    private void addBookingsInDb() {

        bookingRepository.save(currentBookingForItem1);
        bookingRepository.save(currentBookingForItem2);
        bookingRepository.save(pastBookingForItem1);
        bookingRepository.save(pastBookingForItem2);
        bookingRepository.save(futureBookingForItem1);
        bookingRepository.save(futureBookingForItem2);
        bookingRepository.save(waitingBookingForItem1);
        bookingRepository.save(waitingBookingForItem2);
        bookingRepository.save(rejectedBookingForItem1);
        bookingRepository.save(rejectedBookingForItem2);
    }
}