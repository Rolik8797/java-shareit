package ru.practicum.shareit.exception;

public class UserNotFoundException extends ModelNotFoundException {

    public UserNotFoundException(Long id) {
        super(id, "User");
    }
}