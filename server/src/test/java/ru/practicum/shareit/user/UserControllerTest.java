package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;
    private User testUserOne;
    private User testUserTwo;
    private List<User> users;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        testUserOne = User
                .userBuilder()
                .id(1L)
                .name("Test1")
                .email("test1@test.ru")
                .build();

        testUserTwo = User
                .userBuilder()
                .id(2L)
                .name("Test2")
                .email("test2@test.ru")
                .build();

        users = List.of(testUserOne, testUserTwo);
    }

    @Test
    void shouldCreate() throws Exception {
        when(userService.save(any())).thenReturn(UserMapper.toUserDto(testUserOne));
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(testUserOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserOne.getId()))
                .andExpect(jsonPath("$.name").value(testUserOne.getName()))
                .andExpect(jsonPath("$.email").value(testUserOne.getEmail()));
    }

    @Test
    void shouldUpdate() throws Exception {
        UserDto userDto = UserDto
                .userDtoBuilder()
                .id(1L)
                .name("newName")
                .email("test1@test.ru")
                .build();
        when(userService.update(any(), any())).thenReturn(userDto);
        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void shouldFindById() throws Exception {
        when(userService.findById(1L)).thenReturn(UserMapper.toUserDto(testUserOne));
        mockMvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUserOne.getId()))
                .andExpect(jsonPath("$.name").value(testUserOne.getName()))
                .andExpect(jsonPath("$.email").value(testUserOne.getEmail()));
    }

    @Test
    void shouldFindAll() throws Exception {
        when(userService.findAll(0, 10)).thenReturn(UserMapper.toUserDto(users));
        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testUserOne.getId()))
                .andExpect(jsonPath("$[0].name").value(testUserOne.getName()))
                .andExpect(jsonPath("$[0].email").value(testUserOne.getEmail()))
                .andExpect(jsonPath("$[1].id").value(testUserTwo.getId()))
                .andExpect(jsonPath("$[1].name").value(testUserTwo.getName()))
                .andExpect(jsonPath("$[1].email").value(testUserTwo.getEmail()));
    }

    @Test
    void shouldDelete() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, Mockito.times(1)).delete(1L);
    }
}