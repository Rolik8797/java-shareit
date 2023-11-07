package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.ConstantsUtil.USER_ID_HEADER;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody ItemRequestDto itemDto,
            @NotNull @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Create request, userId={}", userId);
        return itemRequestClient.create(itemDto, userId);
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> findById(
            @PathVariable Long requestId,
            @NotNull @RequestHeader(USER_ID_HEADER) Long userId
    ) {
        log.info("Get request {}, userId={}", requestId, userId);
        return itemRequestClient.findById(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@NotNull @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get requests by user, userId={}", userId);
        return itemRequestClient.findByUserId(userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> findAll(
            @NotNull @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Get all requests, userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.findAll(userId, from, size);
    }
}