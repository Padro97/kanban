package main.exceptions;

public class KVTaskClientGetTokenException extends RuntimeException {
    public KVTaskClientGetTokenException(final String message) {
        super(message);
    }
}
