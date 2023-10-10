package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import ru.practicum.shareit.exception.DataExistException;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.UriBuilderUtil;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    private final UriBuilderUtil uriBuilderUtil;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) throws DataExistException {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/users");
        Logger.logRequest(HttpMethod.POST, uriComponents.toUriString(), userDto.toString());
        return ResponseEntity.status(201).body(userService.addUser(userDto));
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable long userId) {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/users/{userId}");
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), "пусто");
        return ResponseEntity.ok().body(userService.getUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() throws DataExistException {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/users");
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), "пусто");
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PatchMapping("{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable long userId, @RequestBody UserDto userDto) throws DataExistException {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/users/{userId}");
        Logger.logRequest(HttpMethod.PATCH, uriComponents.toUriString(), userDto.toString());
        return ResponseEntity.ok().body(userService.updateUser(userId, userDto));
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable long userId) {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/users/{userId}");
        Logger.logRequest(HttpMethod.DELETE, uriComponents.toUriString(), "пусто");
        userService.removeUser(userId);
        return ResponseEntity.ok().build();
    }
}