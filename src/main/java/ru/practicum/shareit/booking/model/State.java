package ru.practicum.shareit.booking.model;

import java.util.Optional;

public enum State {

    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<State> stringToState(String state) {
        for (State value : State.values()) {
            if (value.name().equals(state)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

}