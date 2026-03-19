package www.stock.az.exception;

/**
 * 409 Conflict – duplicate or business rule violation.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
