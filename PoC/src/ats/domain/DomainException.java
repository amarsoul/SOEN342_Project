package ats.domain;

// this is an exception when a precondtion of the system operation is not met, the message is shown to the user
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}