package ru.practicum.shareit.booking.service.finder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class BookingFinderFactory {
    private final Map<BookingService.BookingState, BookingFinder> finders = new HashMap<>();

    @Autowired
    public BookingFinderFactory(Set<BookingFinder> set) {
        set.forEach(f -> finders.put(f.getSearchType(), f));
    }

    public BookingFinder getFinder(BookingService.BookingState state) {
        return finders.get(state);
    }
}