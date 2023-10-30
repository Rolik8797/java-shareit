package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getByOwnerId(
            @RequestHeader(Constants.headerUserId) Long userId,
            @RequestParam(defaultValue = Constants.pageFrom) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = Constants.pageSize) @Positive Integer size) {
        log.info("Получен запрос GET /items " + userId);
        return itemClient.getByOwnerId(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(Constants.headerUserId) Long userId,
                                          @PathVariable Long id) {
        log.info("Получен запрос GET /items/id  запрос на вещь с id" + id);
        return itemClient.getById(userId, id);
    }

    @Validated
    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(Constants.headerUserId) Long userId,
                                      @Valid @RequestBody ItemExtendedDto itemExtendedDto) {
        log.info("Получен запрос POST /items " + itemExtendedDto);
        return itemClient.add(userId, itemExtendedDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(Constants.headerUserId) Long userId,
                                         @PathVariable Long id,
                                         @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PATCH /items/id " + "!Обновление вещи с id" + id + " на " + itemDto + " юзер с id" + userId);
        return itemClient.update(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос POST /items/id " + id);
        itemClient.deleteItem(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestHeader(Constants.headerUserId) Long userId,
            @RequestParam String text,
            @RequestParam(defaultValue = Constants.pageFrom) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = Constants.pageSize) @Positive Integer size) {
        log.info("Получен запрос PATCH /items/search " + text);
        return itemClient.search(text, userId, from, size);
    }

    @PostMapping("{id}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(Constants.headerUserId) long userId,
                                             @PathVariable long id,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, id, commentDto);
    }
}