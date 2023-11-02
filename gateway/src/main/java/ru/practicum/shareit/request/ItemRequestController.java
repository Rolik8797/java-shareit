package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.constains.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader(Constants.headerUserId) Long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос POST  " + userId);
        return itemRequestClient.add(userId, itemRequestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @RequestHeader(Constants.headerUserId) Long userId,
            @PathVariable Long id) {
        log.info("Получен запрос GET вещи с id: " + id + "пользователя с id: " + userId);
        return itemRequestClient.getById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getByRequestorId(
            @RequestHeader(Constants.headerUserId) Long userId) {
        log.info("Получен запрос GET в соответсвии с RequestId");
        return itemRequestClient.getByRequestorId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader(Constants.headerUserId) Long userId,
            @RequestParam(defaultValue = Constants.pageFrom) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = Constants.pageSize) @Positive Integer size) {
        log.info("Получен запрос GET всех вещей пользователя с id: " + userId);
        return itemRequestClient.getAll(userId, from, size);
    }
}