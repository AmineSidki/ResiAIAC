package org.aminesidki.resiaiac.exception;

public class BucketNotFoundException extends RuntimeException{
    public BucketNotFoundException(String message) {
        super(message);
    }
}
