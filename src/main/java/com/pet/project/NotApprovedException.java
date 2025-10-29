package com.pet.project;

public class NotApprovedException extends RuntimeException {
    public NotApprovedException(String message) {
        super(message);
    }
}
