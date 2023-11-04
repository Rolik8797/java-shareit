package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getById(long userId);

    UserDto save(UserDto user);

    UserDto update(UserDto user);

    void deleteById(long userId);
}