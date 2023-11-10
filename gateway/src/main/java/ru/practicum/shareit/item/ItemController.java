package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;

import static ru.practicum.shareit.constants.ConstantsUtil.USER_ID_HEADER;


@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody ItemDto itemDto,
            @NotNull @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Create item, userId={}", userId);
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> update(
            @RequestBody ItemDto itemDto,
            @NotNull @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId
    ) {
        log.info("Update item {}, userId={}", itemId, userId);
        return itemClient.update(itemId, itemDto, userId);
    }

    @GetMapping(value = "/{itemId}")
    public ResponseEntity<Object> findById(
            @PathVariable Long itemId,
            @NotNull @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.findById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByOwnerId(@NotNull @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get items by owner, ownerId={}", userId);
        return itemClient.findByOwnerId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByNameOrDescription(
            @RequestParam String text,
            @NotNull @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Get items by name or description, text={}, userId={}", text, userId);
        if (text.isBlank()) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        } else {
            return itemClient.findByNameOrDescription(text, userId);
        }
    }

    @PostMapping(value = "/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto,
            @NotNull @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Create comment, itemId={}, userId={}", itemId, userId);
        return itemClient.createComment(itemId, commentDto, userId);
    }
}