package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получен запрос GET /users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(
            @PathVariable Long id) {
        log.info("Получен запрос GET /users/id " + id);
        return userService.getById(id);
    }

    @PostMapping
    public UserDto createUser(
            @Validated(Create.class)
            @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users " + userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id,
                              @Validated(Update.class)
                          @RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH /users/id " + "!Обновление пользователя с id " + id + " на " + userDto);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable Long id) {
        log.info("Получен запрос POST /users/id " + id);
        userService.deleteUser(id);
    }
}