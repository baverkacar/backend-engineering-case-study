package com.dreamgames.backendengineeringcasestudy.controller.advice;

import com.dreamgames.backendengineeringcasestudy.exception.UserCanNotEnterTournamentException;
import com.dreamgames.backendengineeringcasestudy.exception.UserEnteredTournamentBeforeException;
import com.dreamgames.backendengineeringcasestudy.exception.UserExistsException;
import com.dreamgames.backendengineeringcasestudy.exception.UserNotFoundException;
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

    /**
     * Handles exceptions when a user cannot enter a tournament due to eligibility issues.
     * This includes conditions where the user does not meet the required level or coin criteria.
     *
     * @param userCanNotEnterTournamentException The exception that captures why the user cannot enter the tournament.
     * @return A ResponseEntity containing an ExceptionModel detailing the exception and the corresponding HTTP status.
     */
    @ExceptionHandler(value = UserCanNotEnterTournamentException.class)
    public ResponseEntity<ExceptionModel> handleUserNotFoundException(UserCanNotEnterTournamentException userCanNotEnterTournamentException){
        HttpStatus exceptionStatus = HttpStatus.BAD_REQUEST;
        ExceptionModel exceptionDTO = ExceptionModel.convertExceptionToExceptionDTO(exceptionStatus, userCanNotEnterTournamentException.getMessage());
        return new ResponseEntity<>(exceptionDTO, exceptionStatus);
    }

    /**
     * Handles exceptions when a user tries to enter a tournament but is found to have already registered in the current tournament.
     * This prevents duplicate entries and maintains the integrity of the tournament participation rules.
     *
     * @param userEnteredTournamentBeforeException The exception that indicates the user has already entered the tournament.
     * @return A ResponseEntity containing an ExceptionModel detailing the exception and the corresponding HTTP status.
     */
    @ExceptionHandler(value = UserEnteredTournamentBeforeException.class)
    public ResponseEntity<ExceptionModel> handleUserNotFoundException(UserEnteredTournamentBeforeException userEnteredTournamentBeforeException){
        HttpStatus exceptionStatus = HttpStatus.CONFLICT;
        ExceptionModel exceptionDTO = ExceptionModel.convertExceptionToExceptionDTO(exceptionStatus, userEnteredTournamentBeforeException.getMessage());
        return new ResponseEntity<>(exceptionDTO, exceptionStatus);
    }
}
