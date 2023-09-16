package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto get(Long id) {
        User user = userStorage.get(id);
        if (user != null) {
            return userToDto(user);
        }
        return null;
    }

    @Override
    public Collection<UserDto> getAll() {
        Collection<User> users = userStorage.getAll();
        return users.stream()
                .map(this::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = dtoToUser(userDto);
        User addedUser = userStorage.add(user);
        return userToDto(addedUser);
    }

    @Override
    public UserDto patch(UserDto userDto, Long id) {
        userDto.setId(id);
        User user = dtoToUser(userDto);
        User patchedUser = userStorage.patch(user);
        return userToDto(patchedUser);
    }

    @Override
    public Boolean delete(Long id) {
        return userStorage.delete(id);
    }

    // Методы преобразования между User и UserDto
    private UserDto userToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    private User dtoToUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}