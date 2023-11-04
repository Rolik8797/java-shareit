package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.booking.BookingItemDto;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemExtendedDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    Long requestId;
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;
    List<CommentDto> comments;
}