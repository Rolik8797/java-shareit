package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto get(Long id) {
        User user = userStorage.get(id);
        return userMapper.userToDto(user);
    }


    @Override
    public Collection<UserDto> getAll() {
        Collection<User> users = userStorage.getAll();
        return users.stream()
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = userMapper.dtoToUser(userDto);
        User addedUser = userStorage.add(user);
        return userMapper.userToDto(addedUser);
    }

    @Override
    public UserDto patch(UserDto userDto, Long id) {
        userDto.setId(id);
        User user = userMapper.dtoToUser(userDto);
        User patchedUser = userStorage.patch(user);
        if (user.getName() != null && !user.getName().isEmpty()) {
            patchedUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            patchedUser.setEmail(user.getEmail());
        }
        return userMapper.userToDto(patchedUser);
    }

    @Override
    public Boolean delete(Long id) {
        return userStorage.delete(id);
    }
}