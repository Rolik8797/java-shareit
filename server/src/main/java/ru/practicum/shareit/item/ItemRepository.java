package ru.practicum.shareit.item;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Primary
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i " +
            " from Item i " +
            "where i.available = true " +
            "  and (lower(i.name) like '%'||lower(:name)||'%' or lower(i.description) like '%'||lower(:description)||'%')")
    List<Item> findAvailableByNameOrDescription(String name, String description, PageRequest pageRequest);

    @Query("select i from Item i where i.owner.id = :ownerId")
    List<Item> findByOwnerId(Long ownerId, PageRequest pageRequest);

    @Query("select count(b.id) > 0 " +
            " from Booking b " +
            "where ( ( :end > b.start and :end < b.end ) or ( :start > b.start and :start < b.end ) ) " +
            "  and b.status = 'APPROVED'" +
            "  and b.item.id = :itemId")
    boolean isBookedInPeriod(Long itemId, LocalDateTime start, LocalDateTime end);

    @Query("select count(b.id) > 0 " +
            " from Booking b " +
            "where b.item.id = :itemId" +
            "  and b.booker.id = :userId" +
            "  and b.status = 'APPROVED'" +
            "  and b.start <= CURRENT_TIMESTAMP")
    boolean wasBookedByUser(Long itemId, Long userId);

    List<Item> findByRequestIdIn(List<Long> requests);
}