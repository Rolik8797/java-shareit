package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.CustomPageRequest;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemWasNotBookedByUserException;
import ru.practicum.shareit.item.dto.CommentDto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private final PageRequest pageRequest = CustomPageRequest.of(0, 10, Sort.by("id").ascending());

    private final User user = User
            .userBuilder()
            .id(1L)
            .name("User")
            .email("test@test.com")
            .build();

    private final Item item = Item
            .itemBuilder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .owner(user)
            .build();

    @Test
    void shouldCreate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);
        assertEquals(ItemMapper.toItemDto(item), itemService.save(1L, ItemMapper.toItemDto(item)));
    }

    @Test
    void shouldUpdate() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertEquals(itemDto, itemService.update(1L, 1L, itemDto));
    }

    @Test
    void shouldFindById() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        when(commentRepository.findByItemId(1L)).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItemId(1L)).thenReturn(new ArrayList<>());
        assertEquals(ItemMapper.toItemDto(item), itemService.findById(1L, 1L));
        assertThrows(ItemNotFoundException.class, () -> itemService.findById(99L, 1L));
    }

    @Test
    void shouldFindByOwnerId() {
        List<Item> items = List.of(item);
        when(itemRepository.findByOwnerId(1L, pageRequest)).thenReturn(items);
        when(commentRepository.findByItemIn(any())).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItemInAndStatus(any(), any())).thenReturn(new ArrayList<>());
        assertEquals(ItemMapper.toItemDto(items), itemService.findAllByOwner(1L, 0, 10));
    }

    @Test
    void shouldFindByNameOrDescription() {
        List<Item> items = List.of(item);
        List<ItemDto> itemDtos = ItemMapper.toItemDto(items);
        when(itemRepository.findAvailableByNameOrDescription("Item", "Item", pageRequest)).thenReturn(items);
        assertEquals(itemDtos, itemService.search("Item", 0, 10));
    }

    @Test
    void shouldCreateComment() {
        Comment comment = Comment
                .builder()
                .id(1L)
                .text("Text")
                .author(user)
                .created(LocalDateTime.now())
                .item(item)
                .build();
        CommentDto commentDto = CommentMapper.toDto(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.wasBookedByUser(1L, 1L)).thenReturn(false);
        assertThrows(ItemWasNotBookedByUserException.class, () -> itemService.createComment(commentDto, 1L, 1L));
        when(itemRepository.wasBookedByUser(1L, 1L)).thenReturn(true);
        assertEquals(comment, itemService.createComment(commentDto, 1L, 1L));
    }
}