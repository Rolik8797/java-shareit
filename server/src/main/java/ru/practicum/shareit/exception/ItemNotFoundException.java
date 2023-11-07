package ru.practicum.shareit.exception;

public class ItemNotFoundException extends ModelNotFoundException {

    public ItemNotFoundException(Long id) {
        super(id, "Item");
    }

    public ItemNotFoundException(String message) {
        super(message);
    }

}