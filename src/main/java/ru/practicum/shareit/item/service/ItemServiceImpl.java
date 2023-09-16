package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto getItem(Long id) {
        Item item = itemStorage.getItem(id);
        itemIdValidator(item);
        return itemToItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return itemStorage.getAllItems()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
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
        Item item = itemStorage.getItem(id);
        itemIdValidator(item);
        itemStorage.removeItem(id);
    }

    @Override
    public Collection<ItemDto> searchItemsByDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getAllItems()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable())
                .map(this::itemToItemDto)
                .collect(Collectors.toList());
    }

    private void itemIdValidator(Item item) {
        if (item == null) {
            throw new NotFoundException("Item not found");
        }
        if (item.getName().isBlank()) {
            throw new NotValidException("Name can't be blank");
        }
        if (item.getDescription().isBlank()) {
            throw new NotValidException("Description can't be blank");
        }
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
        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }
    }

    private void userIdValidator(Long userId) {
        if (!userStorage.getAll().contains(userStorage.get(userId))) {
            throw new NotFoundException(String.format("User with id = %d not found.", userId));
        }
    }

    private ItemDto itemToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner());
        itemDto.setRequest(item.getRequest());
        return itemDto;
    }

    private Item itemDtoToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequest(itemDto.getRequest());
        return item;
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