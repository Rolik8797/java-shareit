package ru.practicum.shareit.request;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperImplTest {
    private final LocalDateTime dateTime = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
    private final User user = User.builder()
            .id(1L)
            .name("Test user 1")
            .email("tester1@ya.ru")
            .build();
    private final List<ItemDto> itemsDto = List.of(ItemDto.builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .available(true)
            .ownerId(user.getId())
            .requestId(1L)
            .build());
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("itemRequest1 description")
            .requestorId(user)
            .created(dateTime)
            .build();
    private final ItemRequestAddDto itemRequestCreateDto = ItemRequestAddDto.builder()
            .description("item description")
            .build();
    @InjectMocks
    private ItemRequestMapperImpl itemRequestMapper;

    @Nested
    class ToItemRequest {
        @Test
        public void shouldReturnItemRequest() {
            ItemRequest result = itemRequestMapper.toItemRequest(itemRequestCreateDto, user, dateTime);

            assertNull(result.getId());
            assertEquals(itemRequestCreateDto.getDescription(), result.getDescription());
            assertEquals(user.getId(), result.getRequestorId().getId());
            assertEquals(user.getName(), result.getRequestorId().getName());
            assertEquals(user.getEmail(), result.getRequestorId().getEmail());
            assertEquals(dateTime, result.getCreated());
        }

        @Test
        public void shouldReturnNull() {
            ItemRequest result = itemRequestMapper.toItemRequest(null, null, null);

            assertNull(result);
        }
    }

    @Nested
    class ToItemRequestDto {
        @Test
        public void shouldReturnItemRequestDto() {
            ItemRequestDto result = itemRequestMapper.toItemRequestDto(itemRequest);

            assertEquals(itemRequest.getId(), result.getId());
            assertEquals(itemRequest.getDescription(), result.getDescription());
            assertEquals(itemRequest.getCreated(), result.getCreated());
        }

        @Test
        public void shouldReturnNull() {
            ItemRequestDto result = itemRequestMapper.toItemRequestDto(null);

            assertNull(result);
        }
    }

    @Nested
    class ToItemRequestExtendedDto {
        @Test
        public void shouldReturnItemRequestExtendedDto() {
            ItemRequestExtendedDto result = itemRequestMapper.toItemRequestExtendedDto(itemRequest, itemsDto);

            assertEquals(itemRequest.getId(), result.getId());
            assertEquals(itemRequest.getDescription(), result.getDescription());
            assertEquals(itemRequest.getCreated(), result.getCreated());
            assertEquals(itemsDto, result.getItems());
        }

        @Test
        public void shouldReturnNull() {
            ItemRequestExtendedDto result = itemRequestMapper.toItemRequestExtendedDto(null, null);

            assertNull(result);
        }
    }
}