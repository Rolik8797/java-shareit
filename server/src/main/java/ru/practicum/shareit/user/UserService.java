package ru.practicum.shareit.user;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAll(Integer from, Integer size);

    UserDto findById(Long id);

    UserDto save(UserDto user);

    UserDto update(Long id, UserDto user);

    void delete(Long id);
}