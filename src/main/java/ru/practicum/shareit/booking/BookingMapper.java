package ru.practicum.shareit.booking;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;


@Component
public class BookingMapper {
    private final ModelMapper modelMapper;

    public BookingMapper() {
        modelMapper = new ModelMapper();
    }

    public BookingDto convertToDto(Booking booking) {
        return modelMapper.map(booking, BookingDto.class);
    }

    public BookingDtoShort convertToDtoShort(Booking booking) {
        return modelMapper.map(booking, BookingDtoShort.class);
    }

    public Booking convertFromDto(BookingInputDto bookingInputDto) {
        return modelMapper.map(bookingInputDto, Booking.class);
    }
}