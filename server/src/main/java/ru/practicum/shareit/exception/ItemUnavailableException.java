package ru.practicum.shareit.exception;

public class ItemUnavailableException extends RuntimeException {

    public ItemUnavailableException() {
        super("Item is unavailable");
    }
}