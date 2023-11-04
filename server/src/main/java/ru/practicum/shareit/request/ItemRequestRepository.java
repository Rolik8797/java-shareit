package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;


import java.util.List;


public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long>  {

    List<ItemRequest> findByUser(User user);

    @Query("select r from ItemRequest r where r.user.id <> :userId")
    Page<ItemRequest> findItemRequest(Long userId, Pageable pageable);
}