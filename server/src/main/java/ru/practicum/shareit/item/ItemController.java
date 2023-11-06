package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;

import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

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
    public List<ItemExtendedDto> getByOwnerId(
            @RequestHeader(Constants.headerUserId) Long userId,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM) Integer from,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) Integer size) {
        log.info("Получен запрос GET /items " + userId);
        return itemService.getByOwnerId(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{id}")
    public ItemExtendedDto getById(@RequestHeader(Constants.headerUserId) Long userId,
                                   @PathVariable Long id) {
        log.info("Получен запрос GET /items/id  запрос на вещь с id" + id);
        return itemService.getById(userId, id);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(Constants.headerUserId) Long userId,
                       @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Получен запрос POST /items " + itemDto);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader(Constants.headerUserId) Long userId,
                          @PathVariable Long id,
                          @Validated(Update.class) @RequestBody ItemDto itemDto) {
        log.info("Получен запрос PATCH /items/id " + "!Обновление вещи с id" + id + " на " + itemDto + " юзер с id" + userId);
        return itemService.update(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос POST /items/id " + id);
        itemService.delete(id);
    }


    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM) Integer from,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) Integer size) {
        log.info("Получен запрос PATCH /items/search " + text);
        return itemService.search(text, PageRequest.of(from / size, size));
    }

    @PostMapping("{id}/comment")
    public CommentDto addComment(@RequestHeader(Constants.headerUserId) long userId,
                                 @PathVariable long id,
                                @RequestBody CommentRequestDto commentRequestDto) {
        return itemService.addComment(userId, id, commentRequestDto);
    }

}