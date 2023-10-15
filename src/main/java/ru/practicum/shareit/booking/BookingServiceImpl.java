package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingListDto;

import ru.practicum.shareit.booking.model.Booking;

import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookings;
    private final UserRepository users;
    private final ItemRepository items;
    private final BookingMapper mapper;

    @Override
    public BookingDtoResponse createBooking(Long bookerId, BookingDto bookingDto) {
        bookingDto.setStatus(Status.WAITING);
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Дата окончания бронирования не может быть раньше даты начала или равна");
        }
        Item item = items.findById(bookingDto.getItemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Предмета с id=%s нет", bookingDto.getItemId())));
        if (!item.getOwner().getId().equals(bookerId)) {
            if (item.getAvailable()) {
                User user = users.findById(bookerId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format("Пользователя с id=%s нет", bookerId)));
                Booking booking = mapper.mapToBookingFromBookingDto(bookingDto);
                booking.setItem(item);
                booking.setBooker(user);
                return mapper.mapToBookingDtoResponse(bookings.save(booking));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Вещь с id=%s недоступна для бронирования", item.getId()));
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Владелец не может забронировать свою вещь");
        }
    }

    @Override
    public BookingDtoResponse approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookings.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Бронирования с id=%s нет", bookingId)));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Невозможно изменить статус брони со статусом " + booking.getStatus());
        }
        if (booking.getItem().getOwner().getId().equals(ownerId)) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            return mapper.mapToBookingDtoResponse(bookings.save(booking));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id=%s не является владельцем вещи с id=%s", ownerId, booking.getItem().getOwner().getId()));
        }
    }

    @Override
    public BookingDtoResponse getBookingByIdForOwnerAndBooker(Long bookingId, Long userId) {
        Booking booking = bookings.findById(bookingId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирования с id=" + bookingId + " нет"));
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователь с id=%s не является автором бронирования или владельцем вещи, к которой относится бронирование", userId));
        }
        return mapper.mapToBookingDtoResponse(booking);
    }

    @Override
    public BookingListDto getAllBookingsForUser(Pageable pageable, Long userId, String state) {
        if (!users.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Пользователя с id=%s не существует", userId));
        } else {
            return getListBookings(pageable, state, userId, false);
        }
    }

    @Override
    public BookingListDto getAllBookingsForItemsUser(Pageable pageable, Long userId, String state) {
        if (!users.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s не существует", userId));
        }
        if (!items.existsItemByOwnerId(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("У пользователя с id=%s нет зарегистрированых вещей", userId));
        } else {
            return getListBookings(pageable, state, userId, true);
        }

    }

    private BookingListDto getListBookings(Pageable pageable, String state, Long userId, Boolean isOwner) {
        List<Long> itemsId = isOwner ? items.findAllItemIdByOwnerId(userId) : null;
        List<Booking> bookingList;

        switch (State.fromValue(state.toUpperCase())) {
            case ALL:
                bookingList = isOwner ? bookings.findAllByItemIdInOrderByStartDesc(pageable, itemsId)
                        : bookings.findAllByBookerIdOrderByStartDesc(pageable, userId);
                break;
            case CURRENT:
                bookingList = isOwner ? bookings.findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(pageable, itemsId, LocalDateTime.now(), LocalDateTime.now())
                        : bookings.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(pageable, userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookingList = isOwner ? bookings.findAllByItemIdInAndEndIsBeforeOrderByStartDesc(pageable, itemsId, LocalDateTime.now())
                        : bookings.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(pageable, userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookingList = isOwner ? bookings.findAllByItemIdInAndStartIsAfterOrderByStartDesc(pageable, itemsId, LocalDateTime.now())
                        : bookings.findAllByBookerIdAndStartIsAfterOrderByStartDesc(pageable, userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = isOwner ? bookings.findAllByItemIdInAndStatusIsOrderByStartDesc(pageable, itemsId, Status.WAITING)
                        : bookings.findAllByBookerIdAndStatusIsOrderByStartDesc(pageable, userId, Status.WAITING);
                break;
            case REJECTED:
                bookingList = isOwner ? bookings.findAllByItemIdInAndStatusIsOrderByStartDesc(pageable, itemsId, Status.REJECTED)
                        : bookings.findAllByBookerIdAndStatusIsOrderByStartDesc(pageable, userId, Status.REJECTED);
                break;
            default:
                throw new StateException("Unknown state: " + state);
        }

        return BookingListDto.builder()
                .bookings(bookingList.stream().map(mapper::mapToBookingDtoResponse).collect(Collectors.toList()))
                .build();
    }
}