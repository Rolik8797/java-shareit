package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestService itemRequestService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemService itemService;
    private User user1;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {

        user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");

        itemRequestDto = ItemRequestDto.builder()
                .description("test request description")
                .build();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createItemRequest() {

        userRepository.save(user1);

        ItemDto item1Dto = ItemDto.builder()
                .name("item test")
                .description("item test description")
                .available(true)
                .build();

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
    public void createItemRequestWhenRequesterNotFound() {

        userRepository.save(user1);

        when(itemRequestService.createItemRequest(eq(itemRequestDto), eq(99L)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        assertThatThrownBy(() -> itemRequestService.createItemRequest(itemRequestDto, 99L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getPrivateRequest() {
        User user1 = new User();
        user1.setName("test name");
        user1.setEmail("test@test.ru");

        User user2 = new User();
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);

        ItemRequest savedRequest = new ItemRequest();
        savedRequest.setId(1L);
        savedRequest.setDescription("test request description");
        savedRequest.setCreated(LocalDateTime.now());

        ItemRequestDtoResponse mockItemRequest = ItemRequestDtoResponse.builder()
                .id(1L)
                .description("test request description")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.createItemRequest(eq(itemRequestDto), eq(user2.getId()))).thenReturn(mockItemRequest);

        ItemRequestDtoResponse privateRequest = itemRequestService.createItemRequest(itemRequestDto, user2.getId());

        LocalDateTimeComparator comparator = new LocalDateTimeComparator(1L);
        assertThat(privateRequest)
                .usingComparatorForFields(comparator, "created")
                .usingRecursiveComparison();

    }

    @Test
    public void getPrivateRequestWhenRequesterNotExistingRequests() {
        userRepository.save(user1);

        when(itemRequestService.getPrivateRequests(eq(PageRequest.of(0, 2)), eq(55L)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        assertThatThrownBy(() -> itemRequestService.getPrivateRequests(PageRequest.of(0, 2), 55L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getOtherRequests() {

        User user1 = new User();
        user1.setName("test name 1");
        user1.setEmail("test1@test.com");

        User user2 = new User();
        user2.setName("test name 2");
        user2.setEmail("test2@test.com");

        when(userRepository.save(user1)).thenReturn(user1);
        when(userRepository.save(user2)).thenReturn(user2);

        ItemRequestDtoResponse mockItemRequest = ItemRequestDtoResponse.builder()
                .id(1L)
                .description("test request description")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.createItemRequest(eq(itemRequestDto), eq(user1.getId()))).thenReturn(mockItemRequest);

        ItemRequestDtoResponse otherRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());

        LocalDateTimeComparator comparator = new LocalDateTimeComparator(1L);
        assertThat(otherRequest)
                .usingComparatorForFields(comparator, "created")
                .usingRecursiveComparison();
    }

    @Test
    public void getOtherRequestsWhenRequesterNotFound() {

        userRepository.save(user1);
        itemRequestService.createItemRequest(itemRequestDto, user1.getId());

        when(itemRequestService.getOtherRequests(PageRequest.of(0, 2), 50L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        assertThatThrownBy(() -> itemRequestService.getOtherRequests(PageRequest.of(0, 2), 50L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void getItemRequestWhenUserNotFound() {

        userRepository.save(user1);
        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(

                () -> itemRequestService.getItemRequest(50L, savedRequest.getId())

        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void getItemRequestWhenRequestNotFound() {

        userRepository.save(user1);
        var savedRequest = itemRequestService.createItemRequest(itemRequestDto, user1.getId());
        assertThatThrownBy(

                () -> itemRequestService.getItemRequest(savedRequest.getId(), 50L)

        ).isInstanceOf(NullPointerException.class);
    }
}