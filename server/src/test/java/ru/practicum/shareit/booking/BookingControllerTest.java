package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mockMvc;

    private Booking testBooking;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        testBooking = Booking
                .bookingBuilder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(3L))
                .item(Item
                        .itemBuilder()
                        .id(1L)
                        .name("Item")
                        .build())
                .booker(User
                        .userBuilder()
                        .id(2L)
                        .build())
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void shouldCreate() throws Exception {
        when(bookingService.create(any(), any(), any())).thenReturn(testBooking);
        BookingCreateRequestDto requestDto = BookingCreateRequestDto
                .builder()
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(3L))
                .itemId(1L)
                .bookerId(2L)
                .build();
        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.status").value(testBooking.getStatus().name()))
                .andExpect(jsonPath("$.booker.id").value(testBooking.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(testBooking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(testBooking.getItem().getName()));
    }

    @Test
    void shouldApprove() throws Exception {
        testBooking.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(any(), any(), eq(true))).thenReturn(testBooking);
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.status").value(testBooking.getStatus().name()))
                .andExpect(jsonPath("$.booker.id").value(testBooking.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(testBooking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(testBooking.getItem().getName()));
    }

    @Test
    void shouldFindById() throws Exception {
        when(bookingService.findById(eq(1L), eq(1L))).thenReturn(testBooking);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testBooking.getId()))
                .andExpect(jsonPath("$.status").value(testBooking.getStatus().name()))
                .andExpect(jsonPath("$.booker.id").value(testBooking.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(testBooking.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(testBooking.getItem().getName()));
    }

    @Test
    void shouldFindByBookerId() throws Exception {
        when(bookingService.findByBookerId(any(), any(), any(), any())).thenReturn(List.of(testBooking));
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].status").value(testBooking.getStatus().name()))
                .andExpect(jsonPath("$[0].booker.id").value(testBooking.getBooker().getId()))
                .andExpect(jsonPath("$[0].item.id").value(testBooking.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(testBooking.getItem().getName()));
    }

    @Test
    void shouldFindByOwnerId() throws Exception {
        when(bookingService.findByOwnerId(any(), any(), any(), any())).thenReturn(List.of(testBooking));
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testBooking.getId()))
                .andExpect(jsonPath("$[0].status").value(testBooking.getStatus().name()))
                .andExpect(jsonPath("$[0].booker.id").value(testBooking.getBooker().getId()))
                .andExpect(jsonPath("$[0].item.id").value(testBooking.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(testBooking.getItem().getName()));
    }
}