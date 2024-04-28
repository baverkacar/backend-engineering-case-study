package com.dreamgames.backendengineeringcasestudy.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExceptionModel {
    private final HttpStatus httpStatus;
    private final int statusCode;
    private final String message;
    private final LocalDateTime timeStamp;

    public ExceptionModel(HttpStatus httpStatus, int statusCode, String message, LocalDateTime timeStamp) {
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public static ExceptionModel convertExceptionToExceptionDTO(HttpStatus exceptionStatus, String message){
        return new ExceptionModel(
                exceptionStatus,
                exceptionStatus.value(),
                message,
                LocalDateTime.now()
        );
    }
}
