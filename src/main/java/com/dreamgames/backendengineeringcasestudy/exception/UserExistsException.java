package com.dreamgames.backendengineeringcasestudy.exception;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String msg) {
        super(msg);
    }
}
