package www.stock.az.enums;

/**
 * Movement status enumeration
 */
public enum MovementStatus {
    /**
     * Movement is pending approval/processing
     */
    PENDING,

    /**
     * Movement is approved and ready to be executed
     */
    APPROVED,

    /**
     * Movement is in progress
     */
    IN_PROGRESS,

    /**
     * Movement is completed successfully
     */
    COMPLETED,

    /**
     * Movement is cancelled
     */
    CANCELLED,

    /**
     * Movement failed
     */
    FAILED
}
