package ru.practicum.shareit.request;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class LocalDateTimeComparator implements Comparator<LocalDateTime> {
    private final long toleranceInSeconds;

    public LocalDateTimeComparator(long toleranceInSeconds) {

        this.toleranceInSeconds = toleranceInSeconds;

    }

    @Override
    public int compare(LocalDateTime dateTime1, LocalDateTime dateTime2) {

        long diffInSeconds = ChronoUnit.SECONDS.between(dateTime1, dateTime2);

        if (Math.abs(diffInSeconds) <= toleranceInSeconds) {
            return 0;
        } else if (diffInSeconds < 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
