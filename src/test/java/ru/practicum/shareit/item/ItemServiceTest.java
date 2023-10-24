package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.mockito.Mockito;

import java.util.Optional;

public class ItemServiceTest {

    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    private ItemDto item1Dto;

    private ItemDtoUpdate item1UpdateDto;
    private User user1;
    private User user2;
    private ItemRequest itemRequest1;

    @BeforeEach
    public void setUp() {

        item1Dto = ItemDto.builder()
                .name("item test")
                .description("item test description")
                .available(Boolean.TRUE)
                .build();
        item1UpdateDto = ItemDtoUpdate.builder()
                .name("updated name")
                .description("updated description")
                .available(Boolean.FALSE)
                .build();
        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");
        user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");
        itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("item request1 description");
        itemRequest1.setRequester(user2);
        itemRequest1.setCreated(LocalDateTime.now());

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createAndGetItemById() {

        userRepository.save(user1);
        ItemDtoResponse savedItem = ItemDtoResponse.builder()
                .id(1L)
                .name("item test")
                .description("item test description")
                .available(true)
                .build();

        when(itemService.createItem(eq(item1Dto), eq(user1.getId()))).thenReturn(savedItem);
        var createdItem = itemService.createItem(item1Dto, user1.getId());

        when(itemService.getItemByItemId(eq(user1.getId()), eq(createdItem.getId()))).thenReturn(savedItem);
        var findItem = itemService.getItemByItemId(user1.getId(), createdItem.getId());
        assertThat(createdItem).usingRecursiveComparison().ignoringFields("comments").isEqualTo(findItem);
    }

    @Test
    public void notExistingUserCreateItem() {

        when(itemService.createItem(eq(item1Dto), eq(1L)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        assertThatThrownBy(() -> itemService.createItem(item1Dto, 1L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void createItemWithItemRequest() {

        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        item1Dto.setRequestId(itemRequest1.getId());

        ItemDtoResponse savedItem = ItemDtoResponse.builder()
                .id(1L)
                .name("item test")
                .description("item test description")
                .available(true)
                .build();

        when(itemService.createItem(eq(item1Dto), eq(user1.getId()))).thenReturn(savedItem);
        var createdItem = itemService.createItem(item1Dto, user1.getId());

        when(itemService.getItemByItemId(eq(user2.getId()), eq(createdItem.getId()))).thenReturn(savedItem);
        var findItem = itemService.getItemByItemId(user2.getId(), createdItem.getId());
        assertThat(createdItem).usingRecursiveComparison().ignoringFields("comments").isEqualTo(findItem);
    }

    @Test
    public void createItemWithNotExistingItemRequest() {

        when(itemRequestRepository.findById(2L)).thenReturn(Optional.empty());

        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        item1Dto.setRequestId(2L);

        when(itemService.createItem(item1Dto, user1.getId()))
                .thenThrow(new ResponseStatusException(NOT_FOUND));

        assertThatThrownBy(() -> itemService.createItem(item1Dto, user1.getId()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void updateItem() {

        userRepository.save(user1);

        ItemDtoResponse savedItem = ItemDtoResponse.builder()
                .id(1L)
                .name("updated name")
                .description("updated description")
                .available(false)
                .build();
        when(itemService.updateItem(eq(savedItem.getId()), eq(user1.getId()), any(ItemDtoUpdate.class)))
                .thenReturn(savedItem);
        ItemDtoResponse updatedItem = itemService.updateItem(savedItem.getId(), user1.getId(), item1UpdateDto);

        assertThat(updatedItem.getId()).isEqualTo(savedItem.getId());
        assertThat(updatedItem.getName()).isEqualTo(item1UpdateDto.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(item1UpdateDto.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(item1UpdateDto.getAvailable());
    }

    @Test
    public void updateItemWithNotExistingItemId() {

        userRepository.save(user1);
        when(itemService.updateItem(2L, user1.getId(), item1UpdateDto))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> itemService.updateItem(2L, user1.getId(), item1UpdateDto))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void updateItemWithOtherUser() {

        userRepository.save(user1);

        var savedItem = itemService.createItem(item1Dto, user1.getId());
        when(itemService.createItem(eq(item1Dto), eq(user2.getId())))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        assertThatThrownBy(
                () -> itemService.updateItem(savedItem.getId(), user2.getId(), item1UpdateDto)
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void getItemByNotExistingId() {
        userRepository.save(user1);
        itemService.createItem(item1Dto, user1.getId());

        when(itemService.getItemByItemId(user1.getId(), 2L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> itemService.getItemByItemId(user1.getId(), 2L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getItemByIdWithLastAndNextBookings() {

        ItemService itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemMapper, null);

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setOwner(user1);
        savedItem.setName("item test");
        savedItem.setDescription("item test description");
        savedItem.setAvailable(true);

        when(itemMapper.mapToItemDtoResponse(savedItem)).thenReturn(ItemDtoResponse.builder().build());
        when(itemRepository.findById(savedItem.getId())).thenReturn(Optional.of(savedItem));
        when(userRepository.existsById(user1.getId())).thenReturn(true);

        Booking lastBooking = new Booking();
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
        lastBooking.setItem(savedItem);
        lastBooking.setBooker(user2);
        lastBooking.setStatus(Status.APPROVED);

        Booking nextBooking = new Booking();
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));
        nextBooking.setItem(savedItem);
        nextBooking.setBooker(user2);
        nextBooking.setStatus(Status.APPROVED);

        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(savedItem));

        assertThrows(NullPointerException.class, () -> itemService.getItemByItemId(user1.getId(), savedItem.getId()));
    }

    @Test
    void getPersonalItems() {

        ItemService itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemMapper, itemRequestRepository);
        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.existsById(user1.getId())).thenReturn(true);
        when(userRepository.existsById(user2.getId())).thenReturn(true);

        Item savedItem1 = new Item();
        savedItem1.setId(1L);
        savedItem1.setOwner(user1);
        savedItem1.setName("item test 1");
        savedItem1.setDescription("item test description 1");
        savedItem1.setAvailable(true);

        Item savedItem2 = new Item();
        savedItem2.setId(2L);
        savedItem2.setOwner(user2);
        savedItem2.setName("item test 2");
        savedItem2.setDescription("item test description 2");
        savedItem2.setAvailable(true);

        when(itemMapper.mapToItemDtoResponse(savedItem1)).thenReturn(ItemDtoResponse.builder().build());
        when(itemMapper.mapToItemDtoResponse(savedItem2)).thenReturn(ItemDtoResponse.builder().build());
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(pageable, user1.getId()))
                .thenReturn(Arrays.asList(savedItem1, savedItem2));

        ItemListDto result = itemService.getPersonalItems(pageable, user1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0)).isNotNull();

    }

    @Test
    public void getPersonalItemsWithNotExistingUser() {

        ItemService itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemMapper, itemRequestRepository);
        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.existsById(user1.getId())).thenReturn(true);
        when(userRepository.existsById(user2.getId())).thenReturn(true);
        when(userRepository.existsById(99L)).thenReturn(false);

        Item savedItem1 = new Item();
        savedItem1.setId(1L);
        savedItem1.setOwner(user1);
        savedItem1.setName("item test 1");
        savedItem1.setDescription("item test description 1");
        savedItem1.setAvailable(true);

        Item savedItem2 = new Item();
        savedItem2.setId(2L);
        savedItem2.setOwner(user2);
        savedItem2.setName("item test 2");
        savedItem2.setDescription("item test description 2");
        savedItem2.setAvailable(true);

        when(itemMapper.mapToItemDtoResponse(savedItem1)).thenReturn(
                ItemDtoResponse.builder()
                        .comments(Collections.emptySet())
                        .build()
        );
        when(itemMapper.mapToItemDtoResponse(savedItem2)).thenReturn(
                ItemDtoResponse.builder()
                        .comments(Collections.emptySet())
                        .build()
        );

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(pageable, user1.getId()))
                .thenReturn(List.of(savedItem1));

        ItemListDto result = itemService.getPersonalItems(pageable, 99L);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    public void getFoundItems() {

        when(itemService.getFoundItems(PageRequest.of(0, 2), "em2"))
                .thenReturn(new ItemListDto(new ArrayList<>()));

        Item savedItem1 = new Item();
        savedItem1.setId(1L);
        savedItem1.setName("item test 1");
        savedItem1.setDescription("item test description 1");
        savedItem1.setAvailable(true);

        Item savedItem2 = new Item();
        savedItem2.setId(2L);
        savedItem2.setName("item test 2");
        savedItem2.setDescription("item test description 2");
        savedItem2.setAvailable(true);
        when(itemService.getFoundItems(PageRequest.of(0, 2), "test"))
                .thenReturn(new ItemListDto(new ArrayList<>()));

        var itemListDto1 = itemService.getFoundItems(PageRequest.of(0, 2), "em2");
        assertThat(Collections.unmodifiableList(itemListDto1.getItems())).isEmpty();

        var itemListDto2 = itemService.getFoundItems(PageRequest.of(0, 2), "test");
        assertThat(Collections.unmodifiableList(itemListDto2.getItems())).hasSize(0);

    }

    @Test
    public void getFoundItemsWhenSearchTextIsBlank() {

        when(itemService.getFoundItems(any(), eq(" "))).thenReturn(ItemListDto.builder().items(Collections.emptyList()).build());

        ItemListDto foundItems = itemService.getFoundItems(PageRequest.of(0, 2), " ");

        assertThat(foundItems).isNotNull();
        assertThat(foundItems.getItems()).isEmpty();
    }

    @Test
    public void addComment() {

        CommentDto commentDto = CommentDto.builder()
                .text("Nice item, awesome author")
                .build();

        when(userRepository.existsById(user1.getId())).thenReturn(true);
        when(userRepository.existsById(user2.getId())).thenReturn(true);

        Item savedItem1 = new Item();
        savedItem1.setId(1L);
        savedItem1.setOwner(user1);
        savedItem1.setName("item test");
        savedItem1.setDescription("item test description");
        savedItem1.setAvailable(true);

        when(itemRepository.findById(savedItem1.getId())).thenReturn(Optional.of(savedItem1));
        when(itemMapper.mapToItemDtoResponse(savedItem1)).thenReturn(
                ItemDtoResponse.builder()
                        .comments(new HashSet<>())
                        .build()
        );

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));

        Comment savedComment1 = new Comment();
        savedComment1.setId(1L);
        savedComment1.setText(commentDto.getText());
        savedComment1.setCreated(LocalDateTime.now());
        savedComment1.setAuthor(user2);
        savedComment1.setItem(savedItem1);

        when(itemMapper.mapToCommentFromCommentDto(commentDto)).thenReturn(savedComment1);

        CommentDtoResponse savedComment1Response = itemService.addComment(savedItem1.getId(), user2.getId(), commentDto);
        CommentDtoResponse savedComment2Response = itemService.addComment(savedItem1.getId(), user2.getId(), commentDto);

        assertThat(savedComment1Response).isEqualTo(savedComment2Response);
    }

    @Test
    public void addCommentFromUserWithNotExistingBooks() {

        CommentDto commentDto = CommentDto.builder()
                .text("Nice item, awesome author")
                .build();
        when(itemService.addComment(eq(2L), eq(user1.getId()), any(CommentDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        userRepository.save(user1);
        userRepository.save(user2);
        var savedItem1 = itemService.createItem(item1Dto, user1.getId());

        assertThatThrownBy(() -> itemService.addComment(savedItem1.getId(), user2.getId(), commentDto))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void addCommentForNotExistingItem() {

        CommentDto commentDto = CommentDto.builder()
                .text("Nice item, awesome author")
                .build();

        when(itemService.addComment(eq(2L), eq(user1.getId()), any(CommentDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        when(userRepository.existsById(user1.getId())).thenReturn(true);
        when(userRepository.existsById(user2.getId())).thenReturn(true);

        assertThatThrownBy(() -> itemService.addComment(2L, user2.getId(), commentDto))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void addCommentFromNotExistingUser() {

        CommentDto commentDto = CommentDto.builder()
                .text("Nice item, awesome author")
                .build();
        userRepository.save(user1);

        when(itemService.addComment(eq(2L), eq(user1.getId()), any(CommentDto.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        var savedItem1 = itemService.createItem(item1Dto, user1.getId());

        assertThatThrownBy(() -> itemService.addComment(savedItem1.getId(), 50L, commentDto))
                .isInstanceOf(NullPointerException.class);

    }
}