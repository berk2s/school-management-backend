package com.schoolplus.office.web.exceptions;

public class AuthorityNotFoundException extends RuntimeException {
    public AuthorityNotFoundException(String message) {
        super(message);
    }
}
