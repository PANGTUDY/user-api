package com.pangtudy.userapi.user.config;

import com.pangtudy.userapi.user.exception.ConflictException;
import com.pangtudy.userapi.user.exception.NotFoundException;
import com.pangtudy.userapi.user.exception.UnauthorizedException;
import com.pangtudy.userapi.user.model.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<?> handleRuntimeException(NotFoundException e) {
        final ResponseDto responseDto = new ResponseDto("error", e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    @ExceptionHandler(ConflictException.class)
    protected ResponseEntity<?> handleConflictException(ConflictException e) {
        final ResponseDto responseDto = new ResponseDto("error", e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDto);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<?> handleUnauthorizedException(UnauthorizedException e) {
        final ResponseDto responseDto = new ResponseDto("error", e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDto);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        final ResponseDto responseDto = new ResponseDto("error", e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<?> handleHttpMessageNotReadableException(MethodArgumentTypeMismatchException e) {
        final ResponseDto responseDto = new ResponseDto("error", e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final ResponseDto responseDto = new ResponseDto("error", e.getAllErrors().get(0).getDefaultMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

}
