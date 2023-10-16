package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdNot(Pageable pageable, Long requesterId);

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items WHERE ir.requester.id = :requesterId")
    List<ItemRequest> findAllItemRequestsWithItemsByRequesterId(@Param("requesterId") Long requesterId);
}
