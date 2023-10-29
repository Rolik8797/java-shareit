package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.request.model.ItemRequest;

import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;



import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryTest {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final int from = Integer.parseInt(Constants.PAGE_DEFAULT_FROM);
    private final int size = Integer.parseInt(Constants.PAGE_DEFAULT_SIZE);
    private final Pageable pageable = PageRequest.of(from / size, size);
    private final LocalDateTime dateTime = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
    private final User user1 = User.builder()
            .id(1L)
            .name("Test user 1")
            .email("tester1@ya.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("Test user 2")
            .email("tester2@ya.ru")
            .build();
    private final Item item1 = Item.builder()
            .id(1L)
            .name("item name")
            .description("item description")
            .available(true)
            .owner(user1)
            .requestId(1L)
            .build();
    private final ItemRequest itemRequest1 = ItemRequest.builder()
            .id(1L)
            .description("itemRequest1 description")
            .requestorId(user2)
            .created(dateTime)
            .build();

    @BeforeEach
    public void beforeEach() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        itemRepository.save(item1);
    }

    private void checkItemRequest(ItemRequest itemRequest, User user, LocalDateTime dateTime, ItemRequest resultItemRequest) {
        assertEquals(itemRequest.getId(), resultItemRequest.getId());
        assertEquals(itemRequest.getDescription(), resultItemRequest.getDescription());
        assertEquals(user.getId(), resultItemRequest.getRequestorId().getId());
        assertEquals(user.getName(), resultItemRequest.getRequestorId().getName());
        assertEquals(user.getEmail(), resultItemRequest.getRequestorId().getEmail());
        assertEquals(dateTime, resultItemRequest.getCreated());
    }

    @Nested
    class FindByRequesterIdIdOrderByCreatedAsc {
        @Test
        public void shouldGetOne() {
            List<ItemRequest> itemsRequest = itemRequestRepository.findByRequestorId_IdOrderByCreatedAsc(user2.getId());

            assertEquals(1, itemsRequest.size());

            ItemRequest resultItemRequest = itemsRequest.get(0);

            checkItemRequest(itemRequest1, user2, dateTime, resultItemRequest);
        }

        @Test
        public void shouldGetZeroIfNotRequests() {
            List<ItemRequest> itemsRequest = itemRequestRepository.findByRequestorId_IdOrderByCreatedAsc(user1.getId());

            assertTrue(itemsRequest.isEmpty());
        }
    }

    @Nested
    class FindByRequesterIdIdNot {
        @Test
        public void shouldGetZeroIfOwner() {
            List<ItemRequest> itemsRequest = itemRequestRepository.findByRequestorId_IdNot(user2.getId(), pageable)
                    .get().collect(Collectors.toList());

            assertTrue(itemsRequest.isEmpty());
        }

        @Test
        public void shouldGetOneIfNotOwner() {
            List<ItemRequest> itemsRequest = itemRequestRepository.findByRequestorId_IdNot(user1.getId(), pageable)
                    .get().collect(Collectors.toList());

            assertEquals(1, itemsRequest.size());

            ItemRequest resultItemRequest = itemsRequest.get(0);

            checkItemRequest(itemRequest1, user2, dateTime, resultItemRequest);
        }
    }
}