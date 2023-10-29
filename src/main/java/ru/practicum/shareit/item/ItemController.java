package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

import javax.validation.Valid;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemExtendedDto> getPersonalItems(
            @RequestHeader(Constants.headerUserId) Long userId,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) @Positive Integer size) {
        log.info("Получен запрос GET /items " + userId);
        return itemService.getPersonalItems(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{id}")
    public ItemExtendedDto getById(@RequestHeader(Constants.headerUserId) Long userId,
                                   @PathVariable Long id) {
        log.info("Получен запрос GET /items/id  запрос на вещь с id" + id);
        return itemService.getById(userId, id);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(Constants.headerUserId) Long userId,
                              @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Получен запрос POST /items " + itemDto);
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader(Constants.headerUserId) Long userId,
                              @PathVariable Long id,
                              @Validated(Update.class) @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PATCH /items/id " + "!Обновление вещи с id" + id + " на " + itemDto + " юзер с id" + userId);
        return itemService.updateItem(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос POST /items/id " + id);
        itemService.delete(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getFoundItems(
            @RequestParam String text,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) @Positive Integer size) {
        log.info("Получен запрос PATCH /items/search " + text);
        return itemService.getFoundItems(text, PageRequest.of(from / size, size));
    }

    @PostMapping("{id}/comment")
    public CommentDto addComment(@RequestHeader(Constants.headerUserId) long userId,
                                 @PathVariable long id,
                                 @Valid @RequestBody CommentRequestDto commentRequestDto) {
        return itemService.addComment(userId, id, commentRequestDto);
    }
}