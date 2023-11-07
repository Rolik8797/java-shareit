package ru.practicum.shareit.item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.CustomPageRequest;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemWasNotBookedByUserException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> findAllByOwner(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = CustomPageRequest.of(from, size, Sort.by("id").ascending());
        List<Item> items = itemRepository.findByOwnerId(userId, pageRequest);
        setDataFromDb(items, true);
        return ItemMapper.toItemDto(items);
    }

    @Override
    public ItemDto findById(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        item.setComments(commentRepository.findByItemId(id));
        item.setBookings(bookingRepository.findByItemId(id));

        if (Objects.equals(item.getOwner().getId(), userId)) {
            setLastAndNextBooking(item);
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(String query, Integer from, Integer size) {
        PageRequest pageRequest = CustomPageRequest.of(from, size, Sort.by("id").ascending());
        List<Item> items = itemRepository.findAvailableByNameOrDescription(query, query, pageRequest);
        setDataFromDb(items, false);
        return ItemMapper.toItemDto(items);
    }

    @Transactional
    @Override
    public ItemDto save(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long id, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        Item updatedItem = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (!Objects.equals(owner.getId(), updatedItem.getOwner().getId())) {
            throw new ItemNotFoundException("Item id = " + id + " with user id = " + userId + " not found");
        }
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Transactional
    @Override
    public void delete(Long userId, Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        if (Objects.equals(item.getOwner().getId(), userId)) {
            itemRepository.deleteById(id);
        } else {
            throw new ItemNotFoundException("Item id = " + id + " with user id = " + userId + " not found");
        }
    }

    @Transactional
    @Override
    public Comment createComment(CommentDto commentDto, Long itemId, Long userId) {
        Comment comment = CommentMapper.toComment(commentDto);

        if (itemRepository.wasBookedByUser(itemId, userId)) {
            comment.setItem(itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId)));
            comment.setAuthor(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId)));
            return commentRepository.save(comment);
        } else {
            throw new ItemWasNotBookedByUserException();
        }
    }

    private void setDataFromDb(List<Item> items, boolean setLastAndNextBooking) {
        Map<Item, List<Comment>> allComments = commentRepository.findByItemIn(items)
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));

        Map<Item, List<Booking>> allBookings = bookingRepository.findByItemInAndStatus(items, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(Booking::getItem, Collectors.toList()));

        for (Item item : items) {
            item.setComments(allComments.getOrDefault(item, Collections.emptyList()));
            item.setBookings(allBookings.getOrDefault(item, Collections.emptyList()));
            if (setLastAndNextBooking) {
                setLastAndNextBooking(item);
            }
        }
    }

    private void setLastAndNextBooking(Item item) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = item.getBookings();
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (bookings != null) {
            lastBooking = bookings
                    .stream()
                    .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                    .filter(b -> b.getStart().isBefore(now) || b.getStart().equals(now))
                    .max(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            nextBooking = bookings
                    .stream()
                    .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                    .filter(b -> b.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);
        }

        item.setLastBooking(lastBooking);
        item.setNextBooking(nextBooking);
    }
}