package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    List<Item> findAllByOwnerIdOrderByIdAsc(Pageable pageable, Long ownerId);

    Boolean existsItemByOwnerId(Long ownerId);

    @Query("SELECT i.id FROM Item i WHERE i.owner.id = ?1")
    List<Long> findAllItemIdByOwnerId(Long ownerId);
}