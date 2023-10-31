package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.markers.Constants;

import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;

import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestFullContextTest {
    private final UserController userController;
    private final ItemController itemController;
    private final ItemRequestController itemRequestController;

    private void checkItemDto(ItemDto itemDto, ItemDto resultItemDto) {
        assertEquals(itemDto.getId(), resultItemDto.getId());
        assertEquals(itemDto.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), resultItemDto.getAvailable());
        assertEquals(itemDto.getOwnerId(), resultItemDto.getOwnerId());
        assertEquals(itemDto.getRequestId(), resultItemDto.getRequestId());
    }

    @Nested
    class Create {
        @Test
        public void shouldCreate() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@ya.ru")
                    .build();
            userController.createUser(userDto1);

            ItemRequestAddDto itemRequestCreateDto = ItemRequestAddDto.builder()
                    .description("description")
                    .build();

            ItemRequestDto itemRequestDto = itemRequestController.createItemRequest(userDto1.getId(), itemRequestCreateDto);

            assertEquals(1L, itemRequestDto.getId());
            assertEquals(itemRequestCreateDto.getDescription(), itemRequestDto.getDescription());
            assertNotNull(itemRequestDto.getCreated());
        }
    }

    @Nested
    class GetById {
        @Test
        public void shouldGetWithItems() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@ya.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@ya.ru")
                    .build();
            userController.createUser(userDto2);

            ItemRequestAddDto itemRequestCreateDto = ItemRequestAddDto.builder()
                    .description("description")
                    .build();
            ItemRequestDto itemRequestDto = itemRequestController.createItemRequest(userDto1.getId(), itemRequestCreateDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .requestId(itemRequestDto.getId())
                    .build();
            itemController.createItem(itemDto.getOwnerId(), itemDto);

            ItemRequestExtendedDto itemRequestFromController = itemRequestController.getById(userDto1.getId(), itemRequestDto.getId());

            assertEquals(1L, itemRequestFromController.getId());
            assertEquals(itemRequestCreateDto.getDescription(), itemRequestFromController.getDescription());
            assertNotNull(itemRequestFromController.getCreated());

            assertNotNull(itemRequestFromController.getItems());
            assertEquals(1, itemRequestFromController.getItems().size());

            ItemDto itemFromResult = itemRequestFromController.getItems().get(0);

            checkItemDto(itemDto, itemFromResult);
        }
    }

    @Nested
    class GetByRequestorId {
        @Test
        public void shouldGetWithItems() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@ya.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@ya.ru")
                    .build();
            userController.createUser(userDto2);

            ItemRequestAddDto itemRequestCreateDto = ItemRequestAddDto.builder()
                    .description("description")
                    .build();
            ItemRequestDto itemRequestDto = itemRequestController.createItemRequest(userDto1.getId(), itemRequestCreateDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .requestId(itemRequestDto.getId())
                    .build();
            itemController.createItem(itemDto.getOwnerId(), itemDto);

            List<ItemRequestExtendedDto> itemRequestsFromController = itemRequestController.getByRequestorId(userDto1.getId());

            assertEquals(1, itemRequestsFromController.size());

            ItemRequestExtendedDto itemRequestFromController = itemRequestsFromController.get(0);

            assertEquals(1L, itemRequestFromController.getId());
            assertEquals(itemRequestCreateDto.getDescription(), itemRequestFromController.getDescription());
            assertNotNull(itemRequestFromController.getCreated());

            assertNotNull(itemRequestFromController.getItems());
            assertEquals(1, itemRequestFromController.getItems().size());

            ItemDto itemFromResult = itemRequestFromController.getItems().get(0);

            checkItemDto(itemDto, itemFromResult);
        }
    }

    @Nested
    class GetAll {
        @Test
        public void shouldGetAllWhereNotOwner() {
            UserDto userDto1 = UserDto.builder()
                    .id(1L)
                    .name("Test user 1")
                    .email("tester1@ya.ru")
                    .build();
            userController.createUser(userDto1);

            UserDto userDto2 = UserDto.builder()
                    .id(2L)
                    .name("Test user 2")
                    .email("tester2@ya.ru")
                    .build();
            userController.createUser(userDto2);

            ItemRequestAddDto itemRequestCreateDto = ItemRequestAddDto.builder()
                    .description("description")
                    .build();
            ItemRequestDto itemRequestDto = itemRequestController.createItemRequest(userDto1.getId(), itemRequestCreateDto);

            ItemDto itemDto = ItemDto.builder()
                    .id(1L)
                    .name("Test item")
                    .description("Test item description")
                    .available(true)
                    .ownerId(userDto2.getId())
                    .requestId(itemRequestDto.getId())
                    .build();
            itemController.createItem(itemDto.getOwnerId(), itemDto);

            List<ItemRequestExtendedDto> itemRequestsFromController = itemRequestController.getAll(
                    userDto2.getId(),
                    Integer.parseInt(Constants.PAGE_DEFAULT_FROM),
                    Integer.parseInt(Constants.PAGE_DEFAULT_SIZE));

            assertEquals(1, itemRequestsFromController.size());

            ItemRequestExtendedDto itemRequestFromController = itemRequestsFromController.get(0);

            assertEquals(1L, itemRequestFromController.getId());
            assertEquals(itemRequestCreateDto.getDescription(), itemRequestFromController.getDescription());
            assertNotNull(itemRequestFromController.getCreated());

            assertNotNull(itemRequestFromController.getItems());
            assertEquals(1, itemRequestFromController.getItems().size());

            ItemDto itemFromResult = itemRequestFromController.getItems().get(0);

            checkItemDto(itemDto, itemFromResult);
        }
    }
}