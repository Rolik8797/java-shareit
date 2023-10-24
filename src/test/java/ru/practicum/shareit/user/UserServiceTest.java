package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpStatus;

import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
public class UserServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    private UserDto user1;
    private UserDto user2;
    private UserDtoUpdate updateUser1;

    @BeforeEach
    public void setUp() {

        user1 = UserDto.builder()
                .name("test name")
                .email("test@test.ru")
                .build();
        user2 = UserDto.builder()
                .name("test name 2")
                .email("test2@test.ru")
                .build();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createAndGetUser() {

        var savedUser = userService.createUser(user1);
        var findUser = userService.getUserById(1L);

        assertThat(savedUser).usingRecursiveComparison().isEqualTo(findUser);
    }

    @Test
    public void createUserWithDuplicateEmail() {

        UserService userService = mock(UserService.class);
        when(userService.createUser(any(UserDto.class))).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> userService.createUser(user1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void getNotExistUserById() {

        UserService userService = mock(UserService.class);
        when(userService.getUserById(2L)).thenThrow(ResponseStatusException.class);

        assertThatThrownBy(() -> userService.getUserById(2L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getEmptyUsersList() {

        UserService userService = mock(UserService.class);
        when(userService.getUsers()).thenReturn(new UserListDto(Collections.emptyList()));

        UserListDto users = userService.getUsers();

        assertThat(users.getUsers()).isEmpty();
    }

    @Test
    public void getUsersList() {

        UserService userService = mock(UserService.class);

        UserDtoResponse user1 = UserDtoResponse.builder()
                .id(1L)
                .name("test name")
                .email("test@test.ru")
                .build();

        UserDtoResponse user2 = UserDtoResponse.builder()
                .id(2L)
                .name("test name 2")
                .email("test2@test.ru")
                .build();

        List<UserDtoResponse> createdUsers = List.of(user1, user2);
        when(userService.getUsers()).thenReturn(
                new UserListDto(createdUsers));

        UserListDto findUsers = userService.getUsers();

        assertThat(findUsers.getUsers()).hasSize(2);
        assertThat(findUsers.getUsers().get(0))
                .usingRecursiveComparison()
                .isEqualTo(createdUsers.get(0));
        assertThat(findUsers.getUsers().get(1))
                .usingRecursiveComparison()
                .isEqualTo(createdUsers.get(1));
    }

    @Test
    public void updateUser() {

        UserDtoUpdate updateUser1 = UserDtoUpdate.builder()
                .name("update name")
                .email("update-email@test.ru")
                .build();

        User existingUser = new User();
        User updatedUser = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userMapper.mapToUserFromUserDtoUpdate(updateUser1, existingUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        when(userService.updateUser(updateUser1, 1L)).thenReturn(UserDtoResponse.builder()
                .id(1L)
                .name(updateUser1.getName())
                .email(updateUser1.getEmail())
                .build());

        UserDtoResponse updatedUserResponse = userService.updateUser(updateUser1, 1L);

        assertThat(updatedUserResponse.getName()).isEqualTo(updateUser1.getName());
        assertThat(updatedUserResponse.getEmail()).isEqualTo(updateUser1.getEmail());
    }

    @Test
    public void updateUserName() {

        UserService userService = mock(UserService.class);

        updateUser1 = UserDtoUpdate.builder()
                .email("update-email@test.ru")
                .build();

        UserDtoResponse updatedUserResponse = UserDtoResponse.builder()
                .id(1L)
                .name("test name")
                .email("update-email@test.ru")
                .build();

        when(userService.createUser(any(UserDto.class))).thenReturn(updatedUserResponse);
        when(userService.updateUser(any(UserDtoUpdate.class), anyLong())).thenReturn(updatedUserResponse);
        when(userService.getUserById(1L)).thenReturn(updatedUserResponse);

        UserDtoResponse createdUser = userService.createUser(user1);
        userService.updateUser(updateUser1, createdUser.getId());
        var updatedUser1 = userService.getUserById(createdUser.getId());

        assertThat(updatedUser1.getName()).isEqualTo(user1.getName());
        assertThat(updatedUser1.getEmail()).isEqualTo(updateUser1.getEmail());
    }

    @Test
    public void updateUserEmail() {

        UserDto user1 = UserDto.builder()
                .name("test name")
                .email("test@test.ru")
                .build();

        UserDtoUpdate updateUser1 = UserDtoUpdate.builder()
                .name("update name")
                .email("update-email@test.ru")
                .build();

        when(userService.createUser(any(UserDto.class))).thenReturn(UserDtoResponse.builder()
                .id(1L)
                .name(user1.getName())
                .email(user1.getEmail())
                .build());
        when(userService.updateUser(any(UserDtoUpdate.class), eq(1L))).thenReturn(UserDtoResponse.builder()
                .id(1L)
                .name(updateUser1.getName())
                .email(updateUser1.getEmail())
                .build());
        when(userService.getUserById(1L)).thenReturn(UserDtoResponse.builder()
                .id(1L)
                .name(updateUser1.getName())
                .email(updateUser1.getEmail())
                .build());

        UserDtoResponse createdUser = userService.createUser(user1);
        userService.updateUser(updateUser1, createdUser.getId());
        UserDtoResponse updatedUser = userService.getUserById(createdUser.getId());

        assertThat(updatedUser.getName()).isEqualTo(updateUser1.getName());
        assertThat(updatedUser.getEmail()).isEqualTo(updateUser1.getEmail());
    }

    @Test
    public void updateUserDuplicateEmail() {

        user1 = UserDto.builder()
                .name("test name")
                .email("test@test.ru")
                .build();

        user2 = UserDto.builder()
                .name("test name 2")
                .email("test@test.ru")
                .build();

        when(userService.createUser(user1)).thenReturn(UserDtoResponse.builder()
                .id(1L)
                .name(user1.getName())
                .email(user1.getEmail())
                .build());

        when(userService.createUser(user2)).thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> userService.createUser(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void deleteUserById() {

        var savedUser = userService.createUser(user1);

        assertThatThrownBy(() -> userService.deleteUser(savedUser.getId())).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void deleteUserByNotExistId() {

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id=1 нет"))
                .when(userService)
                .deleteUser(1L);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Пользователя с id=1 нет");
    }
}