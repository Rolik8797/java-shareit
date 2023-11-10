package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.BookingState;


import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Validation error");

        ex.getBindingResult().getAllErrors().forEach(er -> log.warn(er.getDefaultMessage()));

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(badRequest)
                .body(ErrorResponse.builder()
                        .status(badRequest.value())
                        .error("Validation error")
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.warn(ex.getMessage());
        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(internalServerError)
                .body(ErrorResponse.builder()
                        .status(internalServerError.value())
                        .error(ex.getMessage())
                        .build());
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn(ex.getMessage());
        ex.printStackTrace();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (Objects.equals(ex.getRequiredType(), BookingState.class)) {
            return ResponseEntity
                    .status(httpStatus)
                    .body(ErrorResponse.builder()
                            .status(httpStatus.value())
                            .error("Unknown state: " + ex.getValue())
                            .build());
        }

        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse
                        .builder()
                        .status(httpStatus.value())
                        .error("Unknown argument: " + ex.getValue())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Throwable ex) {
        log.warn(ex.getMessage());
        ex.printStackTrace();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.builder()
                        .status(httpStatus.value())
                        .error(httpStatus.getReasonPhrase())
                        .build());
    }
}