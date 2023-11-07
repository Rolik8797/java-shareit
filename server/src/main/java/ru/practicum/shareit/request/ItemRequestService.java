package ru.practicum.shareit.request;


import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest create(ItemRequest itemRequest, Long userId);

    List<ItemRequest> findByUserId(Long userId);

    ItemRequest findById(Long requestId, Long userId);

    List<ItemRequest> findAll(Long userId, Integer from, Integer size);
}