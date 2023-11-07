package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return User.userBuilder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .login(userDto.getLogin())
                .name(userDto.getName())
                .build();
    }

    public static List<User> toUser(Iterable<UserDto> dtos) {
        List<User> users = new ArrayList<>();
        for (UserDto userDto : dtos) {
            users.add(toUser(userDto));
        }
        return users;
    }

    public static UserDto toUserDto(User user) {
        return UserDto.userDtoBuilder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> toUserDto(Iterable<User> users) {
        List<UserDto> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(toUserDto(user));
        }
        return dtos;
    }
}