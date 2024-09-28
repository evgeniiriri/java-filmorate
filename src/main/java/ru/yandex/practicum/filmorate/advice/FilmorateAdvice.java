package ru.yandex.practicum.filmorate.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.FilmorateNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmorateValidationException;

import java.time.LocalDateTime;

@ControllerAdvice
public class FilmorateAdvice {

    @ExceptionHandler(FilmorateNotFoundException.class)
    public ResponseEntity<ErrorMessage> NotFoundExceptionHandler(FilmorateNotFoundException e) {
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FilmorateValidationException.class)
    public ResponseEntity<ErrorMessage> ValidationExceptionHandler(FilmorateValidationException e) {
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> anyExceptionHandler(Exception e) {
        return new ResponseEntity<>(new ErrorMessage(e.getMessage(), LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
