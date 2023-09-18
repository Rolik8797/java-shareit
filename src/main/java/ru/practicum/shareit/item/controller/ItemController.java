package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(userIdHeader) Long userId) {
        logger.info("Получен POST запрос на создание элемента");
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader(userIdHeader) Long userId) {
        logger.info("Получен PATCH запрос на обновление элемента с  ID: {}", itemId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        logger.info("Получен GET запрос на поиск элементов с text: {}", text);
        return itemService.searchItemsByDescription(text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeItem(@PathVariable Long itemId) {
        logger.info("Получен DELETE запрос на удаление элемента ID: {}", itemId);
        itemService.removeItem(itemId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable Long itemId) {
        logger.info("Получен GET запрос на извлечение элемента с помощью ID: {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> findAll(@RequestHeader(userIdHeader) Long userId) {
        logger.info("Получен GET запрос на извлечение всех элементов для пользователя с ID: {}", userId);
        return itemService.getAllItemsByUserId(userId);
    }
}