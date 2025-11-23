package com.blog.alcoholblog.exception;

public class WineNotFoundException extends RuntimeException {
    public WineNotFoundException(String message) {
        super(message);
    }
}
