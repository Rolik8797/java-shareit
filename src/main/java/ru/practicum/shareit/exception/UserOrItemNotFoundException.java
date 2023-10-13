package ru.practicum.shareit.exception;


public class UserOrItemNotFoundException extends RuntimeException {
    public UserOrItemNotFoundException(String message) {
        super(message);
    }
}
