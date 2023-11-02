package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import ru.practicum.shareit.constains.Constants;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value(Constants.headerServerUrl) String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(UserDto userDto) {

        return post("", userDto);
    }

    public ResponseEntity<Object> getById(long id) {

        return get("/" + id);
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public ResponseEntity<Object> update(@RequestBody @Valid UserDto userDto, long id) {

        return patch("/" + id, userDto);
    }

    public void deleteUser(long id) {
        delete("/" + id);
    }

}