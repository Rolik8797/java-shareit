package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUserIdOrderById(long userId);

    @Query(value = "select i from Item i where lower(i.name) like %:text% or lower(i.description) like %:text% " +
            "and i.available=true")
    List<Item> findByNameOrDescriptionLike(@Param("text") String text);

    void deleteById(long itemId);
}