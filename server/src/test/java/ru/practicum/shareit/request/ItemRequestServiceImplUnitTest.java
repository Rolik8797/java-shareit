package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private final User user = User
            .userBuilder()
            .id(1L)
            .name("User")
            .email("test@test.ru")
            .build();

    private final ItemRequest request = ItemRequest
            .builder()
            .id(1L)
            .description("Description")
            .requester(user)
            .created(LocalDateTime.now())
            .build();

    @Test
    void shouldCreate() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(requestRepository.save(any())).thenReturn(request);
        assertEquals(request, requestService.create(request, 1L));
    }

    @Test
    void shouldFindByUserId() {
        List<ItemRequest> requests = List.of(request);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(1L)).thenReturn(requests);
        assertEquals(requests, requestService.findByUserId(1L));
    }

    @Test
    void shouldFindById() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findById(1L)).thenReturn(Optional.ofNullable(request));
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());
        when(itemRepository.findByRequestIdIn(any())).thenReturn(new ArrayList<>());
        assertEquals(request, requestService.findById(1L, 1L));
        assertThrows(ItemRequestNotFoundException.class, () -> requestService.findById(99L, 1L));
    }

    @Test
    void shouldFindAll() {
        List<ItemRequest> requests = List.of(request);
        when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(eq(1L), any())).thenReturn(requests);
        when(itemRepository.findByRequestIdIn(any())).thenReturn(new ArrayList<>());
        assertEquals(requests, requestService.findAll(1L, 0, 10));
    }
}