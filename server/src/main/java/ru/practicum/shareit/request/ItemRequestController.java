package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Constants;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;


import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto add(
            @RequestHeader(Constants.headerUserId) Long userId,
            @Valid @RequestBody ItemRequestAddDto itemRequestCreateDto) {
        log.info("Получен запрос POST  " + userId);
        return itemRequestService.add(userId, itemRequestCreateDto);
    }

    @GetMapping("/{id}")
    public ItemRequestExtendedDto getById(
            @RequestHeader(Constants.headerUserId) Long userId,
            @PathVariable Long id) {
        log.info("Получен запрос GET вещи с id: " + id + "пользователя с id: " + userId);
        return itemRequestService.getById(userId, id);
    }

    @GetMapping
    public List<ItemRequestExtendedDto> getByRequestorId(
            @RequestHeader(Constants.headerUserId) Long userId) {
        log.info("Получен запрос GET в соответсвии с RequestId");
        return itemRequestService.getByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestExtendedDto> getAll(
            @RequestHeader(Constants.headerUserId) Long userId,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = Constants.PAGE_DEFAULT_SIZE) Integer size) {
        log.info("Получен запрос GET всех вещей пользователя с id: " + userId);
        return itemRequestService.getAll(userId, PageRequest.of(from / size, size));
    }
}