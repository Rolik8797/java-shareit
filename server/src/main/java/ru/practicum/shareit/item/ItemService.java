package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;


import java.util.Collection;
import java.util.List;

public interface ItemService {
    List<ItemDto> findAllByOwner(Long userId, Integer from, Integer size);

    ItemDto findById(Long id, Long userId);

    Collection<ItemDto> search(String query, Integer from, Integer size);

    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long id, ItemDto itemDto);

    void delete(Long userId, Long id);

    Comment createComment(CommentDto commentDto, Long itemId, Long userId);
}