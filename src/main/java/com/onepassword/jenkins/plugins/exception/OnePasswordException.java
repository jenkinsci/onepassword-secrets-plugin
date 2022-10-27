package com.onepassword.jenkins.plugins.exception;

public class OnePasswordException extends RuntimeException {
    public OnePasswordException(String message) {
        super(message);
    }

    public OnePasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
