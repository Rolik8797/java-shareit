package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingState {

    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingState> stringToState(String stringState) {
        for (BookingState state : BookingState.values()) {
            if (state.name().equals(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }

}