package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.service.BookingService;


import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Validation error");

        ex.getBindingResult().getAllErrors().forEach(er -> {
            String field = "";// ((FieldError) er).getField();
            log.warn("\"" + field + "\" " + er.getDefaultMessage());
        });

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(badRequest)
                .body(ErrorResponse.builder()
                        .status(badRequest.value())
                        .error("Validation error")
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleModelNotFoundException(ModelNotFoundException ex) {
        log.warn(ex.getMessage());
        HttpStatus notFound = HttpStatus.NOT_FOUND;

        return ResponseEntity
                .status(notFound)
                .body(ErrorResponse.builder()
                        .status(notFound.value())
                        .error(ex.getMessage())
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
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn(ex.getMessage());
        ConstraintViolationException cause = (ConstraintViolationException) ex.getCause();
        String constraint = cause.getConstraintName();

        if (constraint != null && constraint.toLowerCase().contains("user_id_fkey")) {
            HttpStatus notFound = HttpStatus.NOT_FOUND;

            return ResponseEntity
                    .status(notFound)
                    .body(ErrorResponse.builder()
                            .status(notFound.value())
                            .error("User not found")
                            .build());
        }

        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(internalServerError)
                .body(ErrorResponse.builder()
                        .status(internalServerError.value())
                        .error(internalServerError.getReasonPhrase())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleItemUnavailableException(ItemUnavailableException ex) {
        log.warn(ex.getMessage());
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(badRequest)
                .body(ErrorResponse.builder()
                        .status(badRequest.value())
                        .error(ex.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleBookingIsAlreadyApprovedException(BookingIsAlreadyApprovedException ex) {
        log.warn(ex.getMessage());
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(badRequest)
                .body(ErrorResponse.builder()
                        .status(badRequest.value())
                        .error(ex.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleItemWasNotBookedByUserException(ItemWasNotBookedByUserException ex) {
        log.warn(ex.getMessage());
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(badRequest)
                .body(ErrorResponse.builder()
                        .status(badRequest.value())
                        .error(ex.getMessage())
                        .build());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn(ex.getMessage());
        ex.printStackTrace();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (Objects.equals(ex.getRequiredType(), BookingService.BookingState.class)) {
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