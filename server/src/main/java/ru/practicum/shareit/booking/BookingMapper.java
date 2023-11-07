package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

import java.util.ArrayList;
import java.util.List;


public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        Item item = bookingDto.getItem() == null ? null : Item.itemBuilder()
                .id(bookingDto.getItem().getId())
                .name(bookingDto.getItem().getName())
                .build();

        return Booking.bookingBuilder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(UserMapper.toUser(bookingDto.getBooker()))
                .item(item)
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto.ItemDtoForBooking itemDtoForBooking =
                BookingDto.ItemDtoForBooking.builder()
                        .id(booking.getItem().getId())
                        .name(booking.getItem().getName())
                        .build();

        return BookingDto.bookingDtoBuilder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(itemDtoForBooking)
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> toBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingDto(booking));
        }
        return dtos;
    }
}