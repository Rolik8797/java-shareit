package ru.practicum.shareit.exception;


public class UserOrItemNotAvailableException extends RuntimeException {
    public UserOrItemNotAvailableException(String message) {
        super(message);
    }
}
