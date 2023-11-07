package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(builderMethodName = "itemDtoBuilder")
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private BookingDtoForItem lastBooking;

    private BookingDtoForItem nextBooking;

    @Builder.Default
    private List<CommentDto> comments = new ArrayList<>();

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingDtoForItem {
        private Long id;
        private Long bookerId;
    }
}