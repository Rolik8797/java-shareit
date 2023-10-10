package ru.practicum.shareit.item;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    private final ModelMapper modelMapper;

    public ItemMapper() {
        modelMapper = new ModelMapper();
    }

    public ItemDto convertToDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    public Item convertFromDto(ItemDto itemDto) {
        return modelMapper.map(itemDto, Item.class);
    }
}