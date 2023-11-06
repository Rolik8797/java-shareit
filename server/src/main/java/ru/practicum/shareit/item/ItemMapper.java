package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "ownerId", expression = "java(item.getOwner().getId())")
    ItemDto toItemDto(Item item);

    @Mapping(target = "id", expression = "java(itemDto.getId())")
    @Mapping(target = "name", expression = "java(itemDto.getName())")
    @Mapping(target = "owner", expression = "java(user)")
    Item toItem(ItemDto itemDto, User user);

    @Mapping(target = "id", expression = "java(item.getId())")
    @Mapping(target = "ownerId", expression = "java(item.getOwner().getId())")
    @Mapping(target = "lastBooking", expression = "java(lastBooking)")
    @Mapping(target = "nextBooking", expression = "java(nextBooking)")
    @Mapping(target = "comments", expression = "java(comments)")
    ItemExtendedDto toItemExtendedDto(Item item, BookingItemDto lastBooking, BookingItemDto nextBooking,
                                      List<CommentDto> comments);

    @Mapping(target = "bookerId", expression = "java(booking.getBooker().getId())")
    BookingItemDto bookingToBookingItemDto(Booking booking);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "created", expression = "java(dateTime)")
    @Mapping(target = "author", expression = "java(user)")
    Comment commentRequestDtoToComment(CommentRequestDto commentRequestDto, LocalDateTime dateTime,
                                       User user, Long itemId);

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    CommentDto commentToCommentDto(Comment comment);

}