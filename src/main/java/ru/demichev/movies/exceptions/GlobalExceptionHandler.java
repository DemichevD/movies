package ru.demichev.movies.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.demichev.movies.dto.FieldValidationErrorDto;
import ru.demichev.movies.dto.RequestValidationErrorDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        List<FieldValidationErrorDto> errors = ex.getFieldErrors().stream()
                .map(error-> new FieldValidationErrorDto(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.error("Error during parameters validation", ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RequestValidationErrorDto(errors));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleControllerException(ControllerException e) {
        log.error("Error during request processing", e);
        return new ResponseEntity<>(
                new ErrorResponse(e.getHttpStatus().toString(),e.getMessage()),
                e.getHttpStatus()
        );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(
            Exception e
    ) {
        log.error("Unknown error during request processing", e);
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


}
