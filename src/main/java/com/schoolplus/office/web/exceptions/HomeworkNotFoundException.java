package com.schoolplus.office.web.exceptions;

public class HomeworkNotFoundException extends RuntimeException {
    public HomeworkNotFoundException(String message) {
        super(message);
    }
}
