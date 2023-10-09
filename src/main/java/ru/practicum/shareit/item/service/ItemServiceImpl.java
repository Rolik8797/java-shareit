package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    private final ItemMapper itemMapper;

    @Override
    public ItemDto getItem(Long id) {
        Item item = itemStorage.getItem(id);
        return itemToItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        List<Item> userItems = itemStorage.getItemsByUserId(userId);
        return userItems.stream()
                .map(this::itemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = itemDtoToItem(itemDto);
        User owner = userStorage.get(userId);
        itemOwnerCheckValidator(owner, userId);
        newItem.setOwner(owner);
        Item createdItem = itemStorage.createItem(newItem);
        return itemToItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        userIdValidator(userId);
        Item oldItem = itemStorage.getItem(itemId);
        itemOwnerNameDescAvailValidator(itemDto, oldItem, userId);
        updateItemFields(oldItem, itemDto);
        Item changedItem = itemStorage.updateItem(oldItem);
        return itemToItemDto(changedItem);
    }

    public void removeItem(Long id) {

        itemStorage.removeItem(id);
    }

    @Override
    public Collection<ItemDto> searchItemsByDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> matchingItems = itemStorage.searchItemsByDescription(text);
        return matchingItems.stream()
                .map(this::itemToItemDto)
                .collect(Collectors.toList());
    }


    private void itemOwnerCheckValidator(User owner, long userId) {
        if (owner == null) {
            throw new NotFoundException(String.format("User with id=%d not found", userId));
        }
    }

    private void itemOwnerNameDescAvailValidator(ItemDto itemDto, Item oldItem, long userId) {
        if (oldItem.getOwner().getId() != userId) {
            throw new NotFoundException("User is not the owner of this item!");
        }
    }

    private void userIdValidator(Long userId) {
        if (userStorage.get(userId) == null) {
            throw new NotFoundException(String.format("User with id = %d not found.", userId));
        }
    }

    private ItemDto itemToItemDto(Item item) {
        return itemMapper.itemToItemDto(item);
    }

    private Item itemDtoToItem(ItemDto itemDto) {
        return itemMapper.itemDtoToItem(itemDto);
    }

    private void updateItemFields(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }
}