package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.util.UriBuilderUtil;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private final UriBuilderUtil uriBuilderUtil;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/items");
        Logger.logRequest(HttpMethod.POST, uriComponents.toUriString(), itemDto.toString());
        return ResponseEntity.status(201).body(itemService.addItem(userId, itemDto));
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/items/{itemId}");
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), "пусто");
        return ResponseEntity.ok().body(itemService.getItemById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/items");
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), "пусто");
        return ResponseEntity.ok().body(itemService.getAllItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        UriComponents uriComponents = uriBuilderUtil.buildUriWithQueryParams("/items", "search", text);
        Logger.logRequest(HttpMethod.GET, uriComponents.toUriString(), "пусто");
        return ResponseEntity.ok().body(itemService.searchItems(text));
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/items/{itemId}");
        Logger.logRequest(HttpMethod.PATCH, uriComponents.toUriString(), itemDto.toString());
        return ResponseEntity.ok().body(itemService.updateItem(userId, itemId, itemDto));
    }

    @DeleteMapping("{itemId}")
    public ResponseEntity<Void> removeItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        itemService.removeItem(userId, itemId);
        UriComponents uriComponents = uriBuilderUtil.buildUri("/items/{itemId}");
        Logger.logRequest(HttpMethod.DELETE, uriComponents.toUriString(), "пусто");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                                 @RequestBody @Valid CommentDto commentDto) {
        UriComponents uriComponents = uriBuilderUtil.buildUri("/items/{itemId}/comment");
        Logger.logRequest(HttpMethod.POST, uriComponents.toUriString(), commentDto.toString());
        return ResponseEntity.ok().body(itemService.addComment(userId, itemId, commentDto));
    }
}