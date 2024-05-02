package com.dreamgames.backendengineeringcasestudy.controller.advice;

import com.dreamgames.backendengineeringcasestudy.exception.*;
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
    public ResponseEntity<ExceptionModel> handleUserCanNotEnterTournamentException(UserCanNotEnterTournamentException userCanNotEnterTournamentException){
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
    public ResponseEntity<ExceptionModel> handleUserEnteredTournamentBeforeException(UserEnteredTournamentBeforeException userEnteredTournamentBeforeException){
        HttpStatus exceptionStatus = HttpStatus.CONFLICT;
        ExceptionModel exceptionDTO = ExceptionModel.convertExceptionToExceptionDTO(exceptionStatus, userEnteredTournamentBeforeException.getMessage());
        return new ResponseEntity<>(exceptionDTO, exceptionStatus);
    }


    /**
     * Handles exceptions when a user has not entered a tournament.
     * Returns a response with HTTP status NOT_FOUND.
     *
     * @param userDidNotEnteredTournamentException the exception thrown when user did not enter the tournament
     * @return a {@link ResponseEntity} containing the exception details and HTTP status NOT_FOUND
     */
    @ExceptionHandler(value = UserDidNotEnteredTournamentException.class)
    public ResponseEntity<ExceptionModel> handleUserDidNotEnteredTournamentException(UserDidNotEnteredTournamentException userDidNotEnteredTournamentException){
        HttpStatus exceptionStatus = HttpStatus.NOT_FOUND;
        ExceptionModel exceptionDTO = ExceptionModel.convertExceptionToExceptionDTO(exceptionStatus, userDidNotEnteredTournamentException.getMessage());
        return new ResponseEntity<>(exceptionDTO, exceptionStatus);
    }

    /**
     * Handles exceptions when a user has not entered a tournament.
     * Returns a response with HTTP status NOT_FOUND.
     *
     * @param noActiveTournamentException the exception thrown when user did not enter the tournament
     * @return a {@link ResponseEntity} containing the exception details and HTTP status NOT_FOUND
     */
    @ExceptionHandler(value = NoActiveTournamentException.class)
    public ResponseEntity<ExceptionModel> handleNoActiveTournamentException(NoActiveTournamentException noActiveTournamentException){
        HttpStatus exceptionStatus = HttpStatus.NOT_FOUND;
        ExceptionModel exceptionDTO = ExceptionModel.convertExceptionToExceptionDTO(exceptionStatus, noActiveTournamentException.getMessage());
        return new ResponseEntity<>(exceptionDTO, exceptionStatus);
    }


    /**
     * Handles exceptions when unclaimed rewards are found for a user.
     * This handler captures the {@link UnClaimedRewardFoundException} and returns an appropriate response.
     *
     * @param unClaimedRewardFoundException the exception that was thrown when unclaimed rewards are detected.
     * @return a {@link ResponseEntity} containing the {@link ExceptionModel} which encapsulates error details,
     *         including a message explaining the error and the HTTP status code.
     */
    @ExceptionHandler(value = UnClaimedRewardFoundException.class)
    public ResponseEntity<ExceptionModel> handleUnClaimedRewardFoundException(UnClaimedRewardFoundException unClaimedRewardFoundException){
        HttpStatus exceptionStatus = HttpStatus.BAD_REQUEST;
        ExceptionModel exceptionDTO = ExceptionModel.convertExceptionToExceptionDTO(exceptionStatus, unClaimedRewardFoundException.getMessage());
        return new ResponseEntity<>(exceptionDTO, exceptionStatus);
    }
}
