package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class UserListDto {
    @JsonValue
    private List<UserDtoResponse> users;
}