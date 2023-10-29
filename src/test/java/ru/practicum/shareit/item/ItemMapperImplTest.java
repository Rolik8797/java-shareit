package ru.practicum.shareit.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ItemMapperImplTest {
    private final LocalDateTime dateTime = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
    private final User user = User.builder()
            .id(1L)
            .name("Test user 1")
            .email("t-r1@ya.ru")
            .build();
    private final CommentRequestDto commentRequestDto = CommentRequestDto.builder()
            .text("commentRequestDto text")
            .build();
    private final Comment comment1 = Comment.builder()
            .id(1L)
            .text("comment1 text")
            .created(dateTime)
            .author(user)
            .itemId(1L)
            .build();
    private final Comment comment2 = Comment.builder()
            .id(2L)
            .text("comment2 text")
            .created(dateTime)
            .author(user)
            .itemId(1L)
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .available(true)
            .owner(user)
            .requestId(1L)
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .start(dateTime.minusYears(10))
            .end(dateTime.minusYears(9))
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .available(true)
            .ownerId(user.getId())
            .requestId(1L)
            .build();
    private final BookingItemDto lastBooking = BookingItemDto.builder()
            .id(1L)
            .bookerId(user.getId())
            .start(dateTime)
            .end(dateTime.plusHours(1))
            .build();
    private final BookingItemDto nextBooking = BookingItemDto.builder()
            .id(2L)
            .bookerId(user.getId())
            .start(dateTime.plusHours(2))
            .end(dateTime.plusHours(3))
            .build();
    @InjectMocks
    private ItemMapperImpl itemMapper;

    @Nested
    class ToItemDto {
        @Test
        public void shouldReturnItemDto() {
            ItemDto result = itemMapper.toItemDto(item);

            assertEquals(item.getId(), result.getId());
            assertEquals(item.getName(), result.getName());
            assertEquals(item.getDescription(), result.getDescription());
            assertEquals(item.getAvailable(), result.getAvailable());
            assertEquals(item.getOwner().getId(), result.getOwnerId());
            assertEquals(item.getRequestId(), result.getRequestId());
        }

        @Test
        public void shouldReturnNull() {
            ItemDto result = itemMapper.toItemDto(null);

            assertNull(result);
        }
    }

    @Nested
    class ToItem {
        @Test
        public void shouldReturnItemDto() {
            Item result = itemMapper.toItem(itemDto, user);

            assertEquals(itemDto.getId(), result.getId());
            assertEquals(itemDto.getName(), result.getName());
            assertEquals(itemDto.getDescription(), result.getDescription());
            assertEquals(itemDto.getAvailable(), result.getAvailable());
            assertEquals(itemDto.getOwnerId(), result.getOwner().getId());
            assertEquals(user.getName(), result.getOwner().getName());
            assertEquals(user.getEmail(), result.getOwner().getEmail());
            assertEquals(itemDto.getRequestId(), result.getRequestId());

        }

        @Test
        public void shouldReturnNull() {
            Item result = itemMapper.toItem(null, null);

            assertNull(result);
        }
    }

    @Nested
    class ToItemExtendedDto {
        @Test
        public void shouldReturnItemExtendedDto() {
            ItemExtendedDto result = itemMapper.toItemExtendedDto(item, lastBooking, nextBooking, List.of(itemMapper.commentToCommentDto(comment1), itemMapper.commentToCommentDto(comment2)));

            assertEquals(item.getId(), result.getId());
            assertEquals(item.getName(), result.getName());
            assertEquals(item.getDescription(), result.getDescription());
            assertEquals(item.getAvailable(), result.getAvailable());
            assertEquals(item.getOwner().getId(), result.getOwnerId());
            assertEquals(item.getRequestId(), result.getRequestId());

            assertEquals(lastBooking.getId(), result.getLastBooking().getId());
            assertEquals(lastBooking.getBookerId(), result.getLastBooking().getBookerId());
            assertEquals(lastBooking.getStart(), result.getLastBooking().getStart());
            assertEquals(lastBooking.getEnd(), result.getLastBooking().getEnd());

            assertEquals(nextBooking.getId(), result.getNextBooking().getId());
            assertEquals(nextBooking.getBookerId(), result.getNextBooking().getBookerId());
            assertEquals(nextBooking.getStart(), result.getNextBooking().getStart());
            assertEquals(nextBooking.getEnd(), result.getNextBooking().getEnd());

            CommentDto commentFromResult1 = result.getComments().get(0);
            CommentDto commentFromResult2 = result.getComments().get(1);

            assertEquals(comment1.getId(), commentFromResult1.getId());
            assertEquals(comment1.getText(), commentFromResult1.getText());
            assertEquals(comment1.getCreated(), commentFromResult1.getCreated());
            assertEquals(comment1.getAuthor().getName(), commentFromResult1.getAuthorName());

            assertEquals(comment2.getId(), commentFromResult2.getId());
            assertEquals(comment2.getText(), commentFromResult2.getText());
            assertEquals(comment2.getCreated(), commentFromResult2.getCreated());
            assertEquals(comment2.getAuthor().getName(), commentFromResult2.getAuthorName());
        }

        @Test
        public void shouldReturnNull() {
            ItemExtendedDto result = itemMapper.toItemExtendedDto(null, null, null, null);
            assertNull(result);
        }
    }

    @Nested
    class BookingToBookingItemDto {
        @Test
        public void shouldReturnBookingItemDto() {
            BookingItemDto result = itemMapper.bookingToBookingItemDto(booking);

            assertEquals(booking.getId(), result.getId());
            assertEquals(booking.getBooker().getId(), result.getBookerId());
            assertEquals(booking.getStart(), result.getStart());
            assertEquals(booking.getEnd(), result.getEnd());
        }

        @Test
        public void shouldReturnNull() {
            BookingItemDto result = itemMapper.bookingToBookingItemDto(null);

            assertNull(result);
        }
    }

    @Nested
    class CommentRequestDtoToComment {
        @Test
        public void shouldReturnComment() {
            Comment result = itemMapper.commentRequestDtoToComment(commentRequestDto, dateTime.plusHours(4),
                    user, item.getId());

            assertNull(result.getId());
            assertEquals(commentRequestDto.getText(), result.getText());
            assertEquals(dateTime.plusHours(4), result.getCreated());
            assertEquals(user.getId(), result.getAuthor().getId());
            assertEquals(user.getName(), result.getAuthor().getName());
            assertEquals(user.getEmail(), result.getAuthor().getEmail());
            assertEquals(item.getId(), result.getItemId());
        }

        @Test
        public void shouldReturnNull() {
            Comment result = itemMapper.commentRequestDtoToComment(null, null,
                    null, null);

            assertNull(result);
        }
    }

    @Nested
    class CommentToCommentDto {
        @Test
        public void shouldReturnCommentDto() {
            CommentDto result = itemMapper.commentToCommentDto(comment1);

            assertEquals(comment1.getId(), result.getId());
            assertEquals(comment1.getText(), result.getText());
            assertEquals(comment1.getCreated(), result.getCreated());
            assertEquals(comment1.getAuthor().getName(), result.getAuthorName());
        }

        @Test
        public void shouldReturnNull() {
            CommentDto result = itemMapper.commentToCommentDto(null);

            assertNull(result);
        }
    }
}