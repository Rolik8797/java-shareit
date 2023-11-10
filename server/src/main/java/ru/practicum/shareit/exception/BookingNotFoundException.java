package ru.practicum.shareit.exception;


public class BookingNotFoundException extends ModelNotFoundException {
    public BookingNotFoundException(Long id) {
        super(id, "Booking");
    }
}