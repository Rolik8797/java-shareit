package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;


import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll(@RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        return userService.findAll(from, size);
    }

    @GetMapping(value = "/{id}")
    public UserDto findById(@PathVariable long id) {
        return userService.findById(id);
    }

    @PostMapping
    public UserDto save(@RequestBody UserDto userDto) {
        return userService.save(userDto);
    }

    @PatchMapping(value = "/{id}")
    public UserDto update(@PathVariable long id,
                          @RequestBody UserDto userDto) {
        return userService.update(id, userDto);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }
}