package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.exception.UserOrItemNotFoundException;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;


    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.convertFromDto(userDto);
        try {
            User userSaved = userRepository.save(user);
            return userMapper.convertToDto(userSaved);
        } catch (DataExistException e) {
            throw new DataExistException(String.format("Пользователь с email %s уже есть в базе", user.getEmail()));
        }
    }


    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        User user = userMapper.convertFromDto(userDto);
        try {
            User targetUser = getUserById(id);
            if (StringUtils.hasLength(user.getEmail())) {
                targetUser.setEmail(user.getEmail());
            }
            if (StringUtils.hasLength(user.getName())) {
                targetUser.setName(user.getName());
            }
            User userSaved = userRepository.save(targetUser);
            return userMapper.convertToDto(userSaved);
        } catch (DataExistException e) {
            throw new DataExistException(String.format("Пользователь с email %s уже есть в базе", user.getEmail()));
        }
    }


    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new UserOrItemNotFoundException(String.format("Пользователь с id %s не найден", userId)));
    }


    @Override
    public UserDto getUser(long userId) {
        User user = getUserById(userId);
        return userMapper.convertToDto(user);
    }


    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users
                .stream()
                .map(userMapper::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    public void removeUser(long id) {
        userRepository.deleteById(id);
    }
}