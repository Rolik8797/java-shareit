package ru.practicum.shareit.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ru.practicum.shareit.booking.BookingRepository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AuthorisationException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    private final LocalDateTime dateTime = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
    private final int from = Integer.parseInt(Constants.PAGE_DEFAULT_FROM);
    private final int size = Integer.parseInt(Constants.PAGE_DEFAULT_SIZE);
    private final Pageable pageable = PageRequest.of(from / size, size);
    private final User user1 = User.builder()
            .id(1L)
            .name("Test user 1")
            .email("t-r1@ya.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("Test user 2")
            .email("t-r2@ya.ru")
            .build();
    private final Item item1 = Item.builder()
            .id(1L)
            .name("item1 name")
            .description("seaRch1 description ")
            .available(true)
            .owner(user1)
            .build();
    private final Booking booking1 = Booking.builder()
            .id(1L)
            .start(dateTime.minusYears(10))
            .end(dateTime.minusYears(9))
            .item(item1)
            .booker(user2)
            .status(Status.APPROVED)
            .build();
    private final Booking booking2 = Booking.builder()
            .id(2L)
            .start(dateTime.minusYears(5))
            .end(dateTime.plusYears(5))
            .item(item1)
            .booker(user2)
            .status(Status.APPROVED)
            .build();
    private final Booking booking3 = Booking.builder()
            .id(3L)
            .start(dateTime.plusYears(8))
            .end(dateTime.plusYears(9))
            .item(item1)
            .booker(user2)
            .status(Status.WAITING)
            .build();
    private final Booking booking4 = Booking.builder()
            .id(4L)
            .start(dateTime.plusYears(9))
            .end(dateTime.plusYears(10))
            .item(item1)
            .booker(user2)
            .status(Status.REJECTED)
            .build();
    private final Item item2 = Item.builder()
            .id(2L)
            .name("item2 name")
            .description("SeARch1 description")
            .available(true)
            .owner(user2)
            .build();
    private final Item item3 = Item.builder()
            .id(3L)
            .name("item3 name")
            .description("itEm3 description")
            .available(false)
            .owner(user1)
            .build();
    private final ItemDto item1DtoToUpdate = ItemDto.builder()
            .id(1L)
            .name("Update item1 name")
            .description("Update seaRch1 description")
            .available(false)
            .build();
    private final ItemDto item1DtoToUpdateBlank = ItemDto.builder()
            .id(1L)
            .name(" ")
            .description(" ")
            .available(null)
            .build();
    private final Comment comment1 = Comment.builder()
            .id(1L)
            .text("comment1 text")
            .created(dateTime)
            .author(user2)
            .itemId(1L)
            .build();
    private final CommentRequestDto comment1RequestDto = CommentRequestDto.builder()
            .text("commentRequestDto text")
            .build();
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemMapperImpl itemMapper;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    @Nested
    class GetByOwnerId {
        @Test
        public void shouldGetTwoItems() {
            when(itemRepository.findByOwnerIdOrderByIdAsc(any(), any())).thenReturn(new PageImpl<>(List.of(item1, item3)));
            when(itemMapper.toItemExtendedDto(any(), any(), any(), any())).thenCallRealMethod();

            itemService.getPersonalItems(user1.getId(), pageable);

            verify(itemRepository, times(1)).findByOwnerIdOrderByIdAsc(any(), any());
            verify(itemMapper, times(2)).toItemExtendedDto(any(), any(), any(), any());
        }

        @Test
        public void shouldGetZeroItems() {
            when(itemRepository.findByOwnerIdOrderByIdAsc(any(), any())).thenReturn(new PageImpl<>(List.of()));

            itemService.getPersonalItems(user1.getId(), pageable);

            verify(itemRepository, times(1)).findByOwnerIdOrderByIdAsc(any(), any());
            verify(itemMapper, never()).toItemExtendedDto(any(), any(), any(), any());
        }
    }

    @Nested
    class GetItemById {
        @Test
        public void shouldGet() {
            when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item2));

            itemService.getItemById(item2.getId());

            verify(itemRepository, times(1)).findById(any());
        }

        @Test
        public void shouldThrowExceptionIfItemIdNotFound() {
            when(itemRepository.findById(item2.getId())).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> itemService.getItemById(item2.getId()));
            assertEquals("Вещи с таким id не существует.", exception.getMessage());
            verify(itemRepository, times(1)).findById(any());
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGetByNotOwner() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
            when(itemMapper.toItemExtendedDto(any(), any(), any(), any())).thenCallRealMethod();

            ItemExtendedDto itemFromService = itemService.getById(user2.getId(), item1.getId());

            assertNull(itemFromService.getLastBooking());
            assertNull(itemFromService.getNextBooking());
            verify(itemRepository, times(1)).findById(any());
            verify(itemMapper, times(1)).toItemExtendedDto(any(), any(), any(), any());
        }

        @Test
        public void shouldGetByOwnerWithLastAndNextBookings() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
            when(itemMapper.toItemExtendedDto(any(), any(), any(), any())).thenCallRealMethod();
            when(bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(List.of(booking2, booking1));
            when(bookingRepository.findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(any(), any(), any()))
                    .thenReturn(List.of(booking3, booking4));
            when(itemMapper.bookingToBookingItemDto(any())).thenCallRealMethod();

            ItemExtendedDto itemFromService = itemService.getById(user1.getId(), item1.getId());

            assertNotNull(itemFromService.getLastBooking());
            assertEquals(booking2.getId(), itemFromService.getLastBooking().getId());
            assertEquals(booking2.getBooker().getId(), itemFromService.getLastBooking().getBookerId());
            assertEquals(booking2.getStart(), itemFromService.getLastBooking().getStart());
            assertEquals(booking2.getEnd(), itemFromService.getLastBooking().getEnd());

            assertNotNull(itemFromService.getNextBooking());
            assertEquals(booking3.getId(), itemFromService.getNextBooking().getId());
            assertEquals(booking3.getBooker().getId(), itemFromService.getNextBooking().getBookerId());
            assertEquals(booking3.getStart(), itemFromService.getNextBooking().getStart());
            assertEquals(booking3.getEnd(), itemFromService.getNextBooking().getEnd());

            verify(itemRepository, times(1)).findById(any());
            verify(itemMapper, times(1)).toItemExtendedDto(any(), any(), any(), any());
            verify(bookingRepository, times(1))
                    .findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any());
            verify(bookingRepository, times(1))
                    .findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(any(), any(), any());
            verify(itemMapper, times(2)).bookingToBookingItemDto(any());
        }

        @Test
        public void shouldGetByOwnerWithEmptyLastAndNextBookings() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
            when(itemMapper.toItemExtendedDto(any(), any(), any(), any())).thenCallRealMethod();
            when(bookingRepository.findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                    .thenReturn(List.of());
            when(bookingRepository.findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(any(), any(), any()))
                    .thenReturn(List.of());

            ItemExtendedDto itemFromService = itemService.getById(user1.getId(), item1.getId());

            assertNull(itemFromService.getLastBooking());
            assertNull(itemFromService.getNextBooking());

            verify(itemRepository, times(1)).findById(any());
            verify(itemMapper, times(1)).toItemExtendedDto(any(), any(), any(), any());
            verify(bookingRepository, times(1))
                    .findByItemIdAndStartBeforeAndStatusEqualsOrderByStartDesc(any(), any(), any());
            verify(bookingRepository, times(1))
                    .findByItemIdAndStartAfterAndStatusEqualsOrderByStartAsc(any(), any(), any());
            verify(itemMapper, never()).bookingToBookingItemDto(any());
        }
    }

    @Nested
    class Add {
        @Test
        public void shouldAdd() {
            when(userService.getUserById(user1.getId())).thenReturn(user1);
            when(itemMapper.toItemDto(any())).thenCallRealMethod();
            when(itemMapper.toItem(any(), any())).thenCallRealMethod();

            itemService.createItem(user1.getId(), itemMapper.toItemDto(item1));

            verify(userService, times(1)).getUserById(user1.getId());
            verify(itemRepository, times(1)).save(item1);
            verify(itemMapper, times(2)).toItemDto(any());
            verify(itemMapper, times(1)).toItem(any(), any());
        }
    }

    @Nested
    class Update {
        @Test
        public void shouldUpdateByOwner() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

            itemService.updateItem(user1.getId(), item1.getId(), item1DtoToUpdate);

            verify(itemRepository, times(1)).findById(any());
            verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());

            Item savedItem = itemArgumentCaptor.getValue();

            assertEquals(item1.getId(), savedItem.getId());
            assertEquals(item1DtoToUpdate.getName(), savedItem.getName());
            assertEquals(item1DtoToUpdate.getDescription(), savedItem.getDescription());
            assertEquals(item1DtoToUpdate.getAvailable(), savedItem.getAvailable());
        }

        @Test
        public void shouldNotUpdateIfBlank() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

            itemService.updateItem(user1.getId(), item1.getId(), item1DtoToUpdateBlank);

            verify(itemRepository, times(1)).findById(any());
            verify(itemRepository, times(1)).save(itemArgumentCaptor.capture());

            Item savedItem = itemArgumentCaptor.getValue();

            assertEquals(item1.getId(), savedItem.getId());
            assertEquals(item1.getName(), savedItem.getName());
            assertEquals(item1.getDescription(), savedItem.getDescription());
            assertEquals(item1.getAvailable(), savedItem.getAvailable());
        }

        @Test
        public void shouldThrowExceptionIfUpdateByNotOwner() {
            when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

            AuthorisationException exception = assertThrows(AuthorisationException.class,
                    () -> itemService.updateItem(user2.getId(), item1.getId(), item1DtoToUpdate));
            assertEquals("Изменение вещи доступно только владельцу.", exception.getMessage());
            verify(itemRepository, times(1)).findById(any());
            verify(itemRepository, never()).save(any());
        }
    }

    @Nested
    class Delete {
        @Test
        public void shouldDeleteIfIdExists() {
            itemService.delete(item1.getId());

            verify(itemRepository, times(1)).deleteById(item1.getId());
        }

        @Test
        public void shouldDeleteIfIdNotExists() {
            itemService.delete(99L);

            verify(itemRepository, times(1)).deleteById(99L);
        }
    }

    @Nested
    class Search {
        @Test
        public void shouldGetEmptyListIfTextIsEmpty() {
            List<ItemDto> itemsFromService = itemService.getFoundItems("", pageable);

            assertTrue(itemsFromService.isEmpty());
            verify(itemRepository, never()).search(any(), any());
        }

        @Test
        public void shouldGetEmptyListIfTextIsBlank() {
            List<ItemDto> itemsFromService = itemService.getFoundItems(" ", pageable);

            assertTrue(itemsFromService.isEmpty());
            verify(itemRepository, never()).search(any(), any());
        }

        @Test
        public void shouldGetIfTextNotBlank() {
            when(itemRepository.search("iTemS", pageable)).thenReturn(new PageImpl<>(List.of(item1, item2)));

            List<ItemDto> itemsFromService = itemService.getFoundItems("iTemS", pageable);

            assertEquals(2, itemsFromService.size());
            verify(itemRepository, times(1)).search(any(), any());
        }
    }

    @Nested
    class AddComment {
        @Test
        public void shouldAdd() {
            when(itemMapper.commentRequestDtoToComment(any(), any(), any(), any())).thenCallRealMethod();
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(any(), any(), any(), any()))
                    .thenReturn(List.of(booking1, booking2));
            when(commentRepository.save(any())).thenReturn(comment1);
            when(itemMapper.commentToCommentDto(any())).thenCallRealMethod();

            CommentDto commentDto = itemService.addComment(user2.getId(), item1.getId(), comment1RequestDto);

            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(any(), any(), any(), any());
            verify(commentRepository, times(1)).save(commentArgumentCaptor.capture());

            Comment savedComment = commentArgumentCaptor.getValue();
            savedComment.setId(commentDto.getId());
            assertEquals(comment1, savedComment);
        }

        @Test
        public void shouldThrowExceptionIfNotFinishedBooking() {
            when(itemMapper.commentRequestDtoToComment(any(), any(), any(), any())).thenCallRealMethod();
            when(userService.getUserById(user2.getId())).thenReturn(user2);
            when(bookingRepository.findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(any(), any(), any(), any()))
                    .thenReturn(List.of());

            BookingException exception = assertThrows(BookingException.class,
                    () -> itemService.addComment(user2.getId(), item1.getId(), comment1RequestDto));
            assertEquals("Пользователь не брал данную вещь в аренду.", exception.getMessage());

            verify(userService, times(1)).getUserById(user2.getId());
            verify(bookingRepository, times(1))
                    .findByItemIdAndBookerIdAndEndIsBeforeAndStatusEquals(any(), any(), any(), any());
            verify(commentRepository, never()).save(any());
        }
    }
}