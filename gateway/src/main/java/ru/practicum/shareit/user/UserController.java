package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Получен запрос GET /users");
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @PathVariable Long id) {
        log.info("Получен запрос GET /users/id " + id);
        return userClient.getById(id);
    }

    @PostMapping
    public ResponseEntity<Object> add(
            @Valid
            @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users " + userDto);
        return userClient.add(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH /users/id " + "!Обновление пользователя с id " + id + " на " + userDto);
        return userClient.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id) {
        log.info("Получен запрос POST /users/id " + id);
        userClient.deleteUser(id);
    }

}