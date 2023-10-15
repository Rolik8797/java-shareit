package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;

import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    private final BookingService bookingService;
    private static BookingDto bookingDto;
    private BookingListDto bookingListDto;
    private static BookingDtoResponse bookingDtoResponse;

    @BeforeEach
    public void setUp() {
        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        ItemShortDto itemShortDto = ItemShortDto.builder()
                .id(bookingDto.getItemId())
                .name("test item")
                .build();
        UserShortDto userShortDto = UserShortDto.builder()
                .id(1L)
                .name("test name")
                .build();
        bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(itemShortDto)
                .booker(userShortDto)
                .status(bookingDto.getStatus())
                .build();
    }

    @Test
    @SneakyThrows
    public void createBooking() {
        when(bookingService.createBooking(anyLong(), any(BookingDto.class))).thenReturn(bookingDtoResponse);
        mvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().json(objectMapper.writeValueAsString(bookingDtoResponse))
                );
    }

    @Test
    @SneakyThrows
    public void createBookingWithIncorrectBookerId() {
        mvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 0))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0)).createBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    @SneakyThrows
    public void createBookingWithIncorrectStart() {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        mvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0)).createBooking(anyLong(), any(BookingDto.class));
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
    }

    @Test
    @SneakyThrows
    public void createBookingWithIncorrectEnd() {
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        mvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0)).createBooking(anyLong(), any(BookingDto.class));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    @SneakyThrows
    public void createBookingWithIncorrectItemId() {
        bookingDto.setItemId(null);
        mvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0)).createBooking(anyLong(), any(BookingDto.class));
        bookingDto.setItemId(1L);
    }

    @Test
    @SneakyThrows
    public void approveBooking() {
        bookingDtoResponse.setStatus(Status.APPROVED);
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoResponse);
        mvc.perform(
                        (patch("/bookings/1"))
                                .header("X-Sharer-User-Id", 1)
                                .param("approved", "true"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingDtoResponse))
                );
        bookingDtoResponse.setStatus(Status.WAITING);
    }

    @Test
    @SneakyThrows
    public void approveBookingWitchIncorrectUserId() {
        mvc.perform(
                        (patch("/bookings/1"))
                                .header("X-Sharer-User-Id", 0)
                                .param("approved", "true"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    public void approveBookingWitchIncorrectBookingId() {
        mvc.perform(
                        (patch("/bookings/0"))
                                .header("X-Sharer-User-Id", 1)
                                .param("approved", "true"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdForOwnerAndBooker() {
        when(bookingService.getBookingByIdForOwnerAndBooker(anyLong(), anyLong())).thenReturn(bookingDtoResponse);
        mvc.perform(
                        get("/bookings/1")
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingDtoResponse))
                );
    }

    @Test
    @SneakyThrows
    public void getBookingByIncorrectBookingIdForOwnerAndBooker() {
        mvc.perform(
                        get("/bookings/0")
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0)).getBookingByIdForOwnerAndBooker(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWithIncorrectUserIdForOwnerAndBooker() {
        mvc.perform(
                        get("/bookings/1")
                                .header("X-Sharer-User-Id", 0))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0)).getBookingByIdForOwnerAndBooker(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWithOutUserIdForOwnerAndBooker() {
        mvc.perform(
                        get("/bookings/1"))
                .andDo(print())
                .andExpectAll(
                        status().isInternalServerError()
                );
        verify(bookingService, times(0)).getBookingByIdForOwnerAndBooker(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUser() {
        bookingListDto = BookingListDto.builder()
                .bookings(List.of(bookingDtoResponse))
                .build();
        when(bookingService.getAllBookingsForUser(any(Pageable.class), anyLong(), anyString()))
                .thenReturn(bookingListDto);
        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "2"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingListDto))
                );
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectState() {
        bookingListDto = BookingListDto.builder()
                .bookings(List.of(bookingDtoResponse))
                .build();
        when(bookingService.getAllBookingsForUser(any(Pageable.class), anyLong(), anyString()))
                .thenThrow(StateException.class);
        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "2")
                                .param("state", "qwe"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectUserId() {
        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 0)
                                .param("from", "0")
                                .param("size", "2"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0))
                .getAllBookingsForUser(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectParamFrom() {
        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "-1")
                                .param("size", "2"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0))
                .getAllBookingsForUser(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForUserWithIncorrectParamSize() {
        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "10000"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0))
                .getAllBookingsForUser(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUser() {
        bookingListDto = BookingListDto.builder()
                .bookings(List.of(bookingDtoResponse))
                .build();
        when(bookingService.getAllBookingsForItemsUser(any(Pageable.class), anyLong(), anyString()))
                .thenReturn(bookingListDto);
        mvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "2"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(bookingListDto))
                );
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUserWithIncorrectUserId() {
        mvc.perform(
                        get("/bookings")
                                .header("X-Sharer-User-Id", 0)
                                .param("from", "0")
                                .param("size", "2"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0))
                .getAllBookingsForItemsUser(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUserWithIncorrectParamFrom() {
        mvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "-1")
                                .param("size", "2"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0))
                .getAllBookingsForItemsUser(any(Pageable.class), anyLong(), anyString());
    }

    @Test
    @SneakyThrows
    public void getAllBookingsForItemsUserWithIncorrectParamSize() {
        mvc.perform(
                        get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1)
                                .param("from", "0")
                                .param("size", "10000"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest()
                );
        verify(bookingService, times(0))
                .getAllBookingsForItemsUser(any(Pageable.class), anyLong(), anyString());
    }
}