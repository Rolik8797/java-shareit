package ru.practicum.shareit.item;


import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        return Item.itemBuilder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequestId())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        Booking lastBookingFromDb = item.getLastBooking();
        ItemDto.BookingDtoForItem lastBooking = lastBookingFromDb == null ? null
                : ItemDto.BookingDtoForItem.builder()
                .id(lastBookingFromDb.getId())
                .bookerId(lastBookingFromDb.getBooker().getId())
                .build();

        Booking nextBookingFromDb = item.getNextBooking();
        ItemDto.BookingDtoForItem nextBooking = nextBookingFromDb == null ? null
                : ItemDto.BookingDtoForItem.builder()
                .id(nextBookingFromDb.getId())
                .bookerId(nextBookingFromDb.getBooker().getId())
                .build();

        List<Comment> commentsFromDb = item.getComments();
        List<CommentDto> comments = commentsFromDb == null || commentsFromDb.isEmpty() ? Collections.emptyList()
                : CommentMapper.toDto(commentsFromDb);

        return ItemDto.itemDtoBuilder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static List<ItemDto> toItemDto(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }
}