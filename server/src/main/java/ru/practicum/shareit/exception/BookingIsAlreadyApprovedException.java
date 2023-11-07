package ru.practicum.shareit.exception;

public class BookingIsAlreadyApprovedException extends RuntimeException {
    public BookingIsAlreadyApprovedException() {
        super("Booking is already approved");
    }
}