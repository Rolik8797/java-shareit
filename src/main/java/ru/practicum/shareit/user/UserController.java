package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoResponse;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.dto.UserListDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Controller
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDtoResponse> createUser(@Valid @RequestBody UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().trim().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDtoResponse createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDtoResponse> getUserById(@PathVariable("id") @Min(1) Long userId) {
        if (userId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        UserDtoResponse user = userService.getUserById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<UserListDto> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }


    @PatchMapping("{id}")
    public ResponseEntity<UserDtoResponse> updateUser(@RequestBody UserDtoUpdate userDtoUpdate, @PathVariable("id") Long userId) {
        if (userId <= 0) {
            return ResponseEntity.badRequest().build();
        }

        UserDtoResponse updatedUser = userService.updateUser(userDtoUpdate, userId);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@Min(1) @PathVariable("id") Long userId) {
        if (userId <= 0) {
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}