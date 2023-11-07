package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;

import ru.practicum.shareit.item.dto.ItemDto;
import java.time.LocalDateTime;
import java.util.Collection;

import static ru.practicum.shareit.markers.ConstantsUtil.USER_ID_HEADER;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> findAllByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                              @RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        return itemService.findAllByOwner(userId, from, size);
    }

    @GetMapping(value = "/{id}")
    public ItemDto findById(@PathVariable long id,
                            @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.findById(id, userId);
    }

    @GetMapping(value = "/search")
    public Collection<ItemDto> search(@RequestParam(name = "text") String query,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size) {
        return itemService.search(query, from, size);
    }

    @PostMapping
    public ItemDto save(@RequestHeader(USER_ID_HEADER) long userId,
                        @RequestBody ItemDto itemDto) {
        return itemService.save(userId, itemDto);
    }

    @PostMapping(value = "/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId,
                                    @RequestBody CommentDto commentDto,
                                    @RequestHeader(USER_ID_HEADER) Long userId) {
        commentDto.setCreated(LocalDateTime.now());
        return CommentMapper.toDto(itemService.createComment(commentDto, itemId, userId));
    }

    @PatchMapping(value = "/{id}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) long userId,
                          @PathVariable long id,
                          @RequestBody ItemDto itemDto) {
        return itemService.update(userId, id, itemDto);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@RequestHeader(USER_ID_HEADER) long userId,
                       @PathVariable long id) {
        itemService.delete(userId, id);
    }
}