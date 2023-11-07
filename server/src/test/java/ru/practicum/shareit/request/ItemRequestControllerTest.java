package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;


import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ItemRequestService requestService;
    @InjectMocks
    private ItemRequestController requestController;
    private MockMvc mockMvc;
    private ItemRequest testRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestController).build();

        testRequest = ItemRequest
                .builder()
                .id(1L)
                .description("Description")
                .build();
    }

    @Test
    void shouldCreate() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(testRequest);
        when(requestService.create(any(), any())).thenReturn(testRequest);
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @Test
    void shouldFindById() throws Exception {
        testRequest = ItemRequest
                .builder()
                .id(1L)
                .description("Description")
                .requester(User
                        .userBuilder()
                        .id(1L)
                        .name("User")
                        .email("user@test.com")
                        .build())
                .items(List.of(Item
                        .itemBuilder()
                        .id(1L)
                        .name("Name")
                        .description("Description")
                        .build()))
                .build();
        when(requestService.findById(any(), any())).thenReturn(testRequest);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRequest.getId()))
                .andExpect(jsonPath("$.description").value(testRequest.getDescription()))
                .andExpect(jsonPath("$.items[0].id").value("1"))
                .andExpect(jsonPath("$.items[0].name").value("Name"))
                .andExpect(jsonPath("$.items[0].description").value("Description"));
    }

    @Test
    void shouldFindByUserId() throws Exception {
        when(requestService.findByUserId(any())).thenReturn(List.of(testRequest));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testRequest.getId()))
                .andExpect(jsonPath("$[0].description").value(testRequest.getDescription()));
    }

    @Test
    void shouldFindAll() throws Exception {
        testRequest = ItemRequest
                .builder()
                .id(1L)
                .description("Description")
                .requester(User
                        .userBuilder()
                        .id(1L)
                        .name("User")
                        .email("user@test.com")
                        .build())
                .items(List.of(Item
                        .itemBuilder()
                        .id(1L)
                        .name("Name")
                        .description("Description")
                        .build()))
                .build();
        when(requestService.findAll(any(), any(), any())).thenReturn(List.of(testRequest));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testRequest.getId()))
                .andExpect(jsonPath("$[0].description").value(testRequest.getDescription()));
    }
}