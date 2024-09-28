package ru.yandex.practicum.filmorate.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDateTime;

@ControllerAdvice
public class FilmorateAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> NotFoundExceptionHandler(NotFoundException e) {
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> ValidationExceptionHandler(ValidationException e) {
        String message = String.format("%s %s %s", LocalDateTime.now(), e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> anyExceptionHandler(Exception e) {
        String message = String.format("%s %s %s", LocalDateTime.now(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
