package com.pet.project.web;

public class NotApprovedException extends RuntimeException {
    public NotApprovedException(String message) {
        super(message);
    }
}
