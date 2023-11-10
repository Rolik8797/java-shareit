package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemInAndStatus(List<Item> item, BookingStatus status);

    @Query("select b " +
            " from Booking b " +
            " join fetch b.item i " +
            "where b.id = :id " +
            "  and i.owner.id = :ownerId")
    Optional<Booking> findByIdAndOwnerId(Long id, Long ownerId);

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    @Query("select b " +
            " from Booking b " +
            "where b.start <= CURRENT_TIMESTAMP " +
            "  and b.end >= CURRENT_TIMESTAMP " +
            "  and b.booker.id = :bookerId ")
    List<Booking> findByBookerIdCurrent(Long bookerId, Pageable pageable);

    @Query("select b " +
            " from Booking b " +
            "where b.end < CURRENT_TIMESTAMP " +
            "  and b.booker.id = :bookerId ")
    List<Booking> findByBookerIdPast(Long bookerId, Pageable pageable);

    @Query("select b " +
            " from Booking b " +
            "where b.start >= CURRENT_TIMESTAMP " +
            "  and b.booker.id = :bookerId ")
    List<Booking> findByBookerIdFuture(Long bookerId, Pageable pageable);

    @Query("select b " +
            " from Booking b " +
            "where b.booker.id = :bookerId " +
            "  and b.status = :status")
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    @Query("select b " +
            " from Booking b " +
            " join fetch b.item i " +
            "where i.owner.id = :ownerId ")
    List<Booking> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("select b " +
            " from Booking b " +
            " join fetch b.item i " +
            "where i.owner.id = :ownerId " +
            "  and b.start <= CURRENT_TIMESTAMP " +
            "  and b.end >= CURRENT_TIMESTAMP ")
    List<Booking> findByOwnerIdCurrent(Long ownerId, Pageable pageable);

    @Query("select b " +
            " from Booking b " +
            " join fetch b.item i " +
            "where i.owner.id = :ownerId " +
            "  and b.end < CURRENT_TIMESTAMP ")
    List<Booking> findByOwnerIdPast(Long ownerId, Pageable pageable);

    @Query("select b " +
            " from Booking b " +
            " join fetch b.item i " +
            "where i.owner.id = :ownerId " +
            "  and b.start >= CURRENT_TIMESTAMP ")
    List<Booking> findByOwnerIdFuture(Long ownerId, Pageable pageable);

    @Query("select b " +
            " from Booking b " +
            " join fetch b.item i " +
            "where i.owner.id = :ownerId " +
            "  and b.status = :status ")
    List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);
}