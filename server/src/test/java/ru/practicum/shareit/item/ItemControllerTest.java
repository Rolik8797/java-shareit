package ru.practicum.shareit.item;

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

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private MockMvc mockMvc;
    private Item testItemOne;
    private User testUser;
    private ItemDto testItemOneDto;
    private ItemDto testItemTwoDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        testUser = User
                .userBuilder()
                .id(1L)
                .name("Owner")
                .email("test@test.ru")
                .build();

        UserDto testUserDto = UserMapper.toUserDto(testUser);

        testItemOne = Item
                .itemBuilder()
                .id(1L)
                .name("Test1")
                .description("Test1")
                .available(true)
                .owner(testUser)
                .nextBooking(Booking.bookingBuilder().id(1L).booker(testUser).build())
                .lastBooking(Booking.bookingBuilder().id(2L).booker(testUser).build())
                .build();

        testItemOneDto = ItemMapper.toItemDto(testItemOne);

        Item testItemTwo = Item
                .itemBuilder()
                .id(1L)
                .name("Test2")
                .description("Test2")
                .available(true)
                .owner(testUser)
                .build();

        testItemTwoDto = ItemMapper.toItemDto(testItemTwo);
    }

    @Test
    void shouldCreate() throws Exception {
        when(itemService.save(any(), any())).thenReturn(testItemOneDto);
        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(testItemOneDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testItemOneDto.getName()))
                .andExpect(jsonPath("$.description").value(testItemOneDto.getDescription()))
                .andExpect(jsonPath("$.available").value(testItemOneDto.getAvailable()));
    }

    @Test
    void shouldUpdate() throws Exception {
        when(itemService.update(any(), any(), any())).thenReturn(testItemOneDto);
        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(testItemOneDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testItemOneDto.getName()))
                .andExpect(jsonPath("$.description").value(testItemOneDto.getDescription()))
                .andExpect(jsonPath("$.available").value(testItemOneDto.getAvailable()));
    }

    @Test
    void shouldFindById() throws Exception {
        when(itemService.findById(any(), any())).thenReturn(ItemMapper.toItemDto(testItemOne));
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testItemOne.getId()))
                .andExpect(jsonPath("$.name").value(testItemOne.getName()))
                .andExpect(jsonPath("$.description").value(testItemOne.getDescription()))
                .andExpect(jsonPath("$.available").value(testItemOne.getAvailable()));
    }

    @Test
    void shouldFindByOwnerId() throws Exception {
        when(itemService.findAllByOwner(any(), any(), any())).thenReturn(List.of(testItemOneDto, testItemTwoDto));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testItemOneDto.getId()))
                .andExpect(jsonPath("$[0].name").value(testItemOneDto.getName()))
                .andExpect(jsonPath("$[0].description").value(testItemOneDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(testItemOneDto.getAvailable()))
                .andExpect(jsonPath("$[1].id").value(testItemTwoDto.getId()))
                .andExpect(jsonPath("$[1].name").value(testItemTwoDto.getName()))
                .andExpect(jsonPath("$[1].description").value(testItemTwoDto.getDescription()))
                .andExpect(jsonPath("$[1].available").value(testItemTwoDto.getAvailable()));
    }

    @Test
    void shouldFindByNameOrDescription() throws Exception {
        when(itemService.search(any(), any(), any())).thenReturn(List.of(testItemOneDto));
        mockMvc.perform(get("/items/search?text=Test1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testItemOneDto.getId()))
                .andExpect(jsonPath("$[0].name").value(testItemOneDto.getName()))
                .andExpect(jsonPath("$[0].description").value(testItemOneDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(testItemOneDto.getAvailable()));
    }

    @Test
    void shouldCreateComment() throws Exception {
        Comment comment = Comment
                .builder()
                .id(1L)
                .text("Comment")
                .item(testItemOne)
                .author(testUser)
                .build();

        when(itemService.createComment(any(), any(), any())).thenReturn(comment);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.text").value(comment.getText()))
                .andExpect(jsonPath("$.authorName").value(comment.getAuthor().getName()));
    }
}