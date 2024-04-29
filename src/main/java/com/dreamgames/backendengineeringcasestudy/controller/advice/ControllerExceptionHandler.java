package com.dreamgames.backendengineeringcasestudy.controller.advice;

import com.dreamgames.backendengineeringcasestudy.exception.user.UserExistsException;
import com.dreamgames.backendengineeringcasestudy.exception.user.UserNotFoundException;
import com.dreamgames.backendengineeringcasestudy.model.ExceptionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * ControllerAdvice to handle exceptions thrown by user-related operations.
 * This class centralizes the handling of specific exceptions, converting them
 * to appropriate HTTP responses.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Handles the UserExistsException and returns an HTTP CONFLICT response.
     *
     * @param userExistsException The exception containing details about the existing user.
     * @return A ResponseEntity containing the {@link ExceptionModel} and the corresponding HttpStatus.
     * @see UserExistsException
     */
    @ExceptionHandler(value = UserExistsException.class)
    public ResponseEntity<ExceptionModel> handleUserExistsException(UserExistsException userExistsException){
        HttpStatus exceptionStatus = HttpStatus.CONFLICT;
        ExceptionModel exceptionDTO = ExceptionModel.convertExceptionToExceptionDTO(exceptionStatus, userExistsException.getMessage());
        return new ResponseEntity<>(exceptionDTO, exceptionStatus);
    }

    /**
     * Handles the UserNotExistsException and returns an HTTP NOT FOUND response.
     *
     * @param userNotFoundException The exception containing details about the non-existing user.
     * @return A ResponseEntity containing the {@link ExceptionModel} and the corresponding HttpStatus.
     * @see UserNotFoundException
     */
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ExceptionModel> handleUserNotFoundException(UserNotFoundException userNotFoundException){
        HttpStatus exceptionStatus = HttpStatus.NOT_FOUND;
        ExceptionModel exceptionDTO = ExceptionModel.convertExceptionToExceptionDTO(exceptionStatus, userNotFoundException.getMessage());
        return new ResponseEntity<>(exceptionDTO, exceptionStatus);
    }
}
