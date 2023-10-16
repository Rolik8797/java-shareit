package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.*;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository items;
    private final UserRepository users;
    private final BookingRepository bookings;
    private final CommentRepository comments;
    private final ItemMapper mapper;
    private final ItemRequestRepository itemRequests;

    @Override
    public ItemDtoResponse createItem(ItemDto item, Long userId) throws ResponseStatusException {
        Item newItem = mapper.mapToItemFromItemDto(item);
        if (item.getRequestId() != null) {
            ItemRequest itemRequest = itemRequests.findById(item.getRequestId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("Запроса с id=%s нет", item.getRequestId())));

            newItem.setRequest(itemRequest);
        }
        newItem.setOwner(users.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s нет", userId))));
        return mapper.mapToItemDtoResponse(items.save(newItem));
    }

    @Override
    public ItemDtoResponse updateItem(Long itemId, Long userId, ItemDtoUpdate item) {
        Item updateItem = items.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Предмета с id=%s нет", itemId)));
        if (!updateItem.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Предмет с id=%s пользователю с id=%s не пренадлежит", itemId, userId));
        }
        return mapper.mapToItemDtoResponse(items.save(mapper.mapToItemFromItemDtoUpdate(item, updateItem)));
    }

    @Override
    public ItemDtoResponse getItemByItemId(Long userId, Long itemId) {
        Item item = items.findById(itemId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Предмета с id=%s нет", itemId)));
        ItemDtoResponse itemDtoResponse = mapper.mapToItemDtoResponse(item);
        if (item.getOwner().getId().equals(userId)) {
            itemDtoResponse.setLastBooking(mapper
                    .mapToBookingShortDto(bookings
                            .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                                    itemId, LocalDateTime.now(), Status.APPROVED).orElse(null)
                    ));
            itemDtoResponse.setNextBooking(mapper.mapToBookingShortDto(bookings
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            itemId, LocalDateTime.now(), Status.APPROVED).orElse(null)
            ));
            return itemDtoResponse;
        }
        return itemDtoResponse;
    }

    @Override
    public ItemListDto getPersonalItems(Pageable pageable, Long userId) {
        if (!users.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s не существует", userId));
        }

        List<Item> personalItems = items.findAllByOwnerIdOrderByIdAsc(pageable, userId);

        Map<Long, Booking> lastBookingMap = new HashMap<>();
        Map<Long, Booking> nextBookingMap = new HashMap<>();

        List<Comment> allComments = comments.findCommentsForItems(personalItems);

        for (Item item : personalItems) {
            List<Booking> itemBookings = item.getBookings();
            if (!itemBookings.isEmpty()) {
                itemBookings.sort(Comparator.comparing(Booking::getStart));
                for (int i = 0; i < itemBookings.size(); i++) {
                    Booking booking = itemBookings.get(i);
                    if (booking.getStart().isBefore(LocalDateTime.now()) && booking.getStatus() == Status.APPROVED) {
                        lastBookingMap.put(item.getId(), booking);
                    }
                    if (booking.getStart().isAfter(LocalDateTime.now()) && booking.getStatus() == Status.APPROVED) {
                        nextBookingMap.put(item.getId(), booking);
                        break;
                    }
                }
            }

            List<Comment> itemComments = allComments.stream()
                    .filter(comment -> comment.getItem().equals(item))
                    .collect(Collectors.toList());

            item.setComments(new HashSet<>(itemComments));
        }

        List<ItemDtoResponse> itemDtoResponses = new ArrayList<>();
        for (Item item : personalItems) {
            Booking lastBooking = lastBookingMap.get(item.getId());
            Booking nextBooking = nextBookingMap.get(item.getId());
            ItemDtoResponse itemDtoResponse = mapper.mapToItemDtoResponse(item);
            itemDtoResponse.setLastBooking(mapper.mapToBookingShortDto(lastBooking));
            itemDtoResponse.setNextBooking(mapper.mapToBookingShortDto(nextBooking));
            itemDtoResponses.add(itemDtoResponse);
        }

        return ItemListDto.builder().items(itemDtoResponses).build();
    }

    @Override
    public ItemListDto getFoundItems(Pageable pageable, String text) {
        if (text.isBlank()) {
            return ItemListDto.builder().items(new ArrayList<>()).build();
        }
        return ItemListDto.builder()
                .items(items.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(pageable, text, text).stream()
                        .map(mapper::mapToItemDtoResponse)
                        .collect(Collectors
                                .toList()))
                .build();
    }

    @Override
    public CommentDtoResponse addComment(Long itemId, Long userId, CommentDto commentDto) {
        if (!bookings.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                Status.APPROVED, LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("У пользователя с id=%s не было ни одной брони на предмет с id=%s", userId, itemId));
        } else {
            User author = users.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s нет", userId)));
            Item item = items.findById(itemId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Предмета с id=%s нет", itemId)));
            Comment comment = mapper.mapToCommentFromCommentDto(commentDto);
            comment.setItem(item);
            comment.setAuthor(author);
            comment.setCreated(LocalDateTime.now());
            return mapper.mapToCommentDtoResponseFromComment(comments.save(comment));
        }
    }
}