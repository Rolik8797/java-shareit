package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.BookingRepository;

import ru.practicum.shareit.exception.UserOrItemNotAvailableException;
import ru.practicum.shareit.exception.UserOrItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.logger.Logger;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        Item item = itemMapper.convertFromDto(itemDto);
        User user = userService.getUserById(userId);
        item.setUserId(user.getId());
        Item itemSaved = itemRepository.save(item);
        return itemMapper.convertToDto(itemSaved);
    }


    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = itemMapper.convertFromDto(itemDto);
        User user = userService.getUserById(userId);
        Item targetItem = itemRepository.findById(itemId).orElseThrow(() ->
                new UserOrItemNotFoundException(String.format("Вещь с id %s не найдена", itemId)));
        if (targetItem.getUserId() != user.getId()) {
            throw new UserOrItemNotFoundException(String.format("У пользователя с id %s не найдена вещь с id %s",
                    userId, itemId));
        }
        if (item.getAvailable() != null) {
            targetItem.setAvailable(item.getAvailable());
        }
        if (StringUtils.hasLength(item.getName())) {
            targetItem.setName(item.getName());
        }
        if (StringUtils.hasLength(item.getDescription())) {
            targetItem.setDescription(item.getDescription());
        }
        Item itemSaved = itemRepository.save(targetItem);
        return itemMapper.convertToDto(itemSaved);
    }


    @Override
    public ItemDto getItemById(long itemId, long userId) {
        userService.getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new UserOrItemNotFoundException(String.format("Вещь с id %s не найдена", itemId)));
        ItemDto itemDto = itemMapper.convertToDto(item);
        List<Booking> bookings = bookingRepository.findByItemIdAndStatus(itemId, Status.APPROVED,
                Sort.by(Sort.Direction.ASC, "start"));
        List<BookingDtoShort> bookingDtoShorts = bookings.stream()
                .map(bookingMapper::convertToDtoShort)
                .collect(Collectors.toList());
        if (item.getUserId() == userId) {   // Бронирования показываем только владельцу вещи
            setBookings(itemDto, bookingDtoShorts);
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId,
                Sort.by(Sort.Direction.ASC, "created"));
        List<CommentDto> commentsDto = comments.stream()
                .map(commentMapper::convertToDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentsDto);
        return itemDto;
    }


    @Override
    public List<ItemDto> getAllItems(long userId) {
        User user = userService.getUserById(userId);
        List<Item> items = itemRepository.findAllByUserIdOrderById(user.getId());
        List<ItemDto> itemsDto = items.stream()
                .map(itemMapper::convertToDto)
                .collect(Collectors.toList());
        Logger.logInfo(HttpMethod.GET, "/items", items.toString());
        List<Booking> bookings = bookingRepository.findAllByOwnerId(userId,
                Sort.by(Sort.Direction.ASC, "start"));
        List<BookingDtoShort> bookingDtoShorts = bookings.stream()
                .map(bookingMapper::convertToDtoShort)
                .collect(Collectors.toList());
        Logger.logInfo(HttpMethod.GET, "/items", bookings.toString());
        List<Comment> comments = commentRepository.findAllByItemIdIn(
                items.stream()
                        .map(Item::getId)
                        .collect(Collectors.toList()),
                Sort.by(Sort.Direction.ASC, "created"));
        itemsDto.forEach(itemDto -> {
            setBookings(itemDto, bookingDtoShorts);
            setComments(itemDto, comments);
        });
        return itemsDto;
    }


    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> items;
        if (text.isBlank()) {
            items = new ArrayList<>();
        } else {
            items = itemRepository.findByNameOrDescriptionLike(text.toLowerCase());
        }
        return items
                .stream()
                .map(itemMapper::convertToDto)
                .collect(Collectors.toList());
    }


    @Override
    public void removeItem(long userId, long itemId) {
        userService.getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new UserOrItemNotFoundException(String.format("Вещь с id %s не найдена", itemId)));
        itemRepository.deleteById(item.getId());
    }


    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Comment comment = commentMapper.convertFromDto(commentDto);
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new UserOrItemNotFoundException(
                String.format("Вещь с id %s не найдена", itemId)));
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndStatus(itemId, userId, Status.APPROVED,
                Sort.by(Sort.Direction.DESC, "start")).orElseThrow(() -> new UserOrItemNotFoundException(
                String.format("Пользователь с id %d не арендовал вещь с id %d.", userId, itemId)));
        if (bookings.isEmpty()) {
            throw new UserOrItemNotAvailableException(
                    String.format("Бронирование не подтверждено.", userId, itemId));
        }
        Logger.logInfo(HttpMethod.POST, "/items/" + itemId + "/comment", bookings.toString());
        bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).findAny().orElseThrow(() ->
                new UserOrItemNotAvailableException(String.format("Пользователь с id %d не может оставлять комментарии вещи " +
                        "с id %d.", userId, itemId)));
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        Comment commentSaved = commentRepository.save(comment);
        return commentMapper.convertToDto(commentSaved);
    }

    private void setBookings(ItemDto itemDto, List<BookingDtoShort> bookings) {
        Map<Long, List<BookingDtoShort>> itemIdToBookings = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        itemDto.setLastBooking(
                itemIdToBookings.getOrDefault(itemDto.getId(), Collections.emptyList()).stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .reduce((a, b) -> b)
                        .orElse(null));

        itemDto.setNextBooking(
                itemIdToBookings.getOrDefault(itemDto.getId(), Collections.emptyList()).stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .reduce((a, b) -> a)
                        .orElse(null));
    }

    private void setComments(ItemDto itemDto, List<Comment> comments) {
        Map<Long, List<Comment>> itemIdToComments = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        itemDto.setComments(
                itemIdToComments.getOrDefault(itemDto.getId(), Collections.emptyList()).stream()
                        .map(commentMapper::convertToDto)
                        .collect(Collectors.toList()));
    }
}