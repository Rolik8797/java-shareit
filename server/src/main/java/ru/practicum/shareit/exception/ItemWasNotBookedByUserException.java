package ru.practicum.shareit.exception;

public class ItemWasNotBookedByUserException extends RuntimeException {

    public ItemWasNotBookedByUserException() {
        super("Item was not booked by user");
    }
}