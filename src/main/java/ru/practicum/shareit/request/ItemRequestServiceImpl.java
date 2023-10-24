package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.ItemRequest;

import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDtoResponse createItemRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        User user = userRepository.findById(requesterId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s нет", requesterId)));
        ItemRequest newRequest = itemRequestMapper.mapToItemRequest(itemRequestDto);
        newRequest.setRequester(user);
        newRequest.setCreated(LocalDateTime.now());
        return itemRequestMapper.mapToItemRequestDtoResponse(itemRequestRepository.save(newRequest));
    }

    @Override
    public ItemRequestListDto getPrivateRequests(PageRequest pageRequest, Long requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s нет", requesterId));
        }

        List<ItemRequest> itemRequestsWithItems = itemRequestRepository.findAllItemRequestsWithItemsByRequesterId(requesterId);

        return ItemRequestListDto.builder()
                .requests(itemRequestMapper.mapToRequestDtoResponseWithMD(itemRequestsWithItems))
                .build();
    }

    @Override
    public ItemRequestListDto getOtherRequests(PageRequest pageRequest, Long requesterId) {
        if (!userRepository.existsById(requesterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s нет", requesterId));
        }
        return ItemRequestListDto.builder()
                .requests(itemRequestMapper.mapToRequestDtoResponseWithMD(itemRequestRepository.findAllByRequesterIdNot(pageRequest, requesterId)
                )).build();
    }

    @Override
    public RequestDtoResponseWithMD getItemRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователя с id=%s нет", userId));
        }
        return itemRequestMapper.mapToRequestDtoResponseWithMD(
                itemRequestRepository.findById(requestId)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        String.format("Запроса с id=%s нет", requestId)
                                )
                        ));
    }

}