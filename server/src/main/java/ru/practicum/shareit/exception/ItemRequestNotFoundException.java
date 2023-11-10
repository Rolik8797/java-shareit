package ru.practicum.shareit.exception;

public class ItemRequestNotFoundException extends ModelNotFoundException {


    public ItemRequestNotFoundException(String message) {
        super(message);
    }

    public ItemRequestNotFoundException(Long id) {
        super(id, "itemRequest");
    }
}