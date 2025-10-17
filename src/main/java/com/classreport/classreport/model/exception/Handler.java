package com.classreport.classreport.model.exception;

import com.classreport.classreport.entity.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class Handler {

    @ExceptionHandler
    public ResponseEntity<Error> NotFoundException(NotFoundException e){
        log.error("Error for Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(e.getMessage(), "Not Found"));
    }

    @ExceptionHandler
    public ResponseEntity<Error> AlreadyExistException(AlreadyExistsException e){
        log.error("Error for Already exist");
        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(new Error(e.getMessage(), "Already Exist"));
    }

    @ExceptionHandler
    public ResponseEntity<Error> TodayHaventLessonException(TodayHaventLessonException e){
        log.error("Error for Today Haven`t Lesson Exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(e.getMessage(), "Today Haven`t Lesson Exception"));
    }


}
