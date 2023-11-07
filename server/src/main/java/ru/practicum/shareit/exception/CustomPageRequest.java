package ru.practicum.shareit.exception;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

public class CustomPageRequest extends PageRequest {
    protected CustomPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
    }

    public static CustomPageRequest of(int from, int size, @NonNull Sort sort) {
        return new CustomPageRequest(from, size, sort);
    }

    public static CustomPageRequest of(int from, int size) {
        return of(from, size, Sort.unsorted());
    }
}