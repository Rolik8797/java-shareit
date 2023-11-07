package ru.practicum.shareit.booking.validator;


import ru.practicum.shareit.booking.BookingCreateRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateBeforeValidator implements ConstraintValidator<ValidPeriod, BookingCreateRequestDto> {
    @Override
    public void initialize(ValidPeriod constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BookingCreateRequestDto requestDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = requestDto.getStart();
        LocalDateTime end = requestDto.getEnd();

        return start != null && end != null && start.isBefore(end);
    }
}