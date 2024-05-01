package com.dreamgames.backendengineeringcasestudy.exception;

public class UserEnteredTournamentBeforeException extends RuntimeException{
    public UserEnteredTournamentBeforeException(String msg) {
        super(msg);
    }
}
