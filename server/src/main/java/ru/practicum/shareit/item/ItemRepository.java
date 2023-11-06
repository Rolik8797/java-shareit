package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByOwnerIdOrderByIdAsc(Long ownerId, Pageable pageable);

    List<Item> findByRequestIdIn(List<Long> ids);

    List<Item> findByRequestId(Long requestId);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%') ) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))) " +
            "AND i.available = true")
    Page<Item> search(String text, Pageable pageable);
}