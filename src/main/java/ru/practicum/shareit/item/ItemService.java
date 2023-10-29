package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemExtendedDto> getPersonalItems(Long userId, Pageable pageable);

    ItemExtendedDto getById(Long userId, Long id);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long id, ItemDto itemDto);

    void delete(Long id);

    List<ItemDto> getFoundItems(String text, Pageable pageable);

    CommentDto addComment(Long userId, Long id, CommentRequestDto commentRequestDto);

    Item getItemById(Long id);
}