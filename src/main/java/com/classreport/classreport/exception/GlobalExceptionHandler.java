package com.classreport.classreport.exception;

import com.classreport.classreport.model.customModel.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Error> NotFoundException(NotFoundException e){
        log.error("Error for Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(e.getMessage(), "Not Found"));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<Error> AlreadyExistException(AlreadyExistsException e){
        log.error("Error for Already exist");
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(new Error(e.getMessage(), "Already Exist"));
    }



}
