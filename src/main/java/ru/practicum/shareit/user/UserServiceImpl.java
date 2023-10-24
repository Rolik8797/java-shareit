package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;

import ru.practicum.shareit.user.model.User;


import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDtoResponse createUser(UserDto user) {
        return userMapper.mapToUserDtoResponse(userRepository.save(userMapper.mapToUserFromUserDto(user)));
    }

    @Override
    public UserDtoResponse getUserById(Long id) {
        return userMapper.mapToUserDtoResponse(userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s нет", id)))
        );
    }

    @Override
    public UserListDto getUsers() {
        return UserListDto.builder()
                .users(userRepository.findAll().stream().map(userMapper::mapToUserDtoResponse).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public UserDtoResponse updateUser(UserDtoUpdate user, Long userId) {
        User updatingUser = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s нет", userId)));
        return userMapper.mapToUserDtoResponse(userRepository.save(userMapper.mapToUserFromUserDtoUpdate(user, updatingUser)));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s нет", id));
        }
        userRepository.deleteById(id);
    }
}