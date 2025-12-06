package www.stock.az.enums;

/**
 * Movement type enumeration for stock operations
 */
public enum MovementType {
    /**
     * Stock incoming operation (purchase, return, etc.)
     */
    STOCK_IN,

    /**
     * Stock outgoing operation (sale, damage, etc.)
     */
    STOCK_OUT,

    /**
     * Stock transfer between warehouses
     */
    TRANSFER
}
