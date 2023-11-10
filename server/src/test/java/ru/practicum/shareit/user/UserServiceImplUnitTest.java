package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import ru.practicum.shareit.exception.CustomPageRequest;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private final UserDto user = UserDto
            .userDtoBuilder()
            .name("Name")
            .email("test@test.ru")
            .build();

    @Test
    void shouldCreate() {
        when(userRepository.save(UserMapper.toUser(user))).thenReturn(UserMapper.toUser(user));
        assertEquals(user, userService.save(user));
    }

    @Test
    void shouldUpdate() {
        User userForUpdate = User
                .userBuilder()
                .id(1L)
                .name("New Name")
                .email("test@test.ru")
                .build();
        UserDto userDto = UserMapper.toUserDto(userForUpdate);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userForUpdate));
        when(userRepository.save(any())).thenReturn(userForUpdate);
        assertEquals(userDto, userService.update(1L, userDto));
    }

    @Test
    void shouldFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(UserMapper.toUser(user)));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertEquals(user, userService.findById(1L));
        assertThrows(UserNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void shouldFindAll() {
        List<UserDto> userDtos = List.of(user);
        Page<User> users = new PageImpl<>(UserMapper.toUser(userDtos));
        PageRequest pageRequest = CustomPageRequest.of(0, 10);
        when(userRepository.findAll(pageRequest)).thenReturn(users);
        assertEquals(userDtos, userService.findAll(0, 10));
    }

    @Test
    void shouldDeleteById() {
        long id = 1L;
        userService.delete(id);
        verify(userRepository, Mockito.times(1)).deleteById(id);
    }
}