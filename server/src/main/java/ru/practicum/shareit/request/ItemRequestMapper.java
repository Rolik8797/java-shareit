package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;


import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<ItemRequestDto.Item> items = itemRequest.getItems() == null ? Collections.emptyList() :
                itemRequest.getItems().stream()
                        .map(i -> ItemRequestDto.Item
                                .builder()
                                .id(i.getId())
                                .name(i.getName())
                                .description(i.getDescription())
                                .available(i.getAvailable())
                                .requestId(i.getRequestId())
                                .build())
                        .collect(Collectors.toUnmodifiableList());

        return ItemRequestDto
                .builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public static List<ItemRequestDto> toItemRequestDto(Iterable<ItemRequest> itemRequests) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            dtos.add(toItemRequestDto(itemRequest));
        }
        return dtos;
    }
}