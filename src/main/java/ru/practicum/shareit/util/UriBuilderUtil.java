package ru.practicum.shareit.util;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class UriBuilderUtil {
    private final String host;
    private final String port;
    private final String protocol;

    public UriBuilderUtil(String host, String port, String protocol) {
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    public UriComponents buildUri(String path) {
        return UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path(path)
                .build();
    }

    public UriComponents buildUriWithQueryParams(String path, String paramName, Object paramValue) {
        return UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path(path)
                .queryParam(paramName, paramValue)
                .build();
    }
}