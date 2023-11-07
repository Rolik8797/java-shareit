package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.CustomPageRequest;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequest create(ItemRequest itemRequest, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        ItemRequest forSave = ItemRequest
                .builder()
                .description(itemRequest.getDescription())
                .requester(userRepository.getReferenceById(userId))
                .created(LocalDateTime.now())
                .build();

        return requestRepository.save(forSave);
    }

    @Override
    public List<ItemRequest> findByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        setAdditionalData(requests);

        return requests;
    }

    @Override
    public ItemRequest findById(Long requestId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() -> new ItemRequestNotFoundException(requestId));
        request.setItems(itemRepository.findByRequestIdIn(List.of(request.getId())));

        return request;
    }

    @Override
    public List<ItemRequest> findAll(Long userId, Integer from, Integer size) {
        PageRequest pageRequest = CustomPageRequest.of(from, size);
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageRequest);
        setAdditionalData(requests);

        return requests;
    }

    private void setAdditionalData(List<ItemRequest> requests) {
        Map<Long, List<Item>> items = itemRepository.findByRequestIdIn(requests
                        .stream()
                        .map(ItemRequest::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(Item::getRequestId, Collectors.toList()));

        requests.forEach(r -> r.setItems(items.get(r.getId())));
    }
}