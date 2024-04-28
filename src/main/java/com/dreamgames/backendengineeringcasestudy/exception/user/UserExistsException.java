package com.dreamgames.backendengineeringcasestudy.exception.user;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String msg) {
        super(msg);
    }
}
