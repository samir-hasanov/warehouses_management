package www.stock.az.enums;

/**
 * Stock operation type for detailed tracking
 */
public enum StockOperationType {
    /**
     * Purchase operation
     */
    PURCHASE,

    /**
     * Sale operation
     */
    SALE,

    /**
     * Return from customer
     */
    RETURN_IN,

    /**
     * Return to supplier
     */
    RETURN_OUT,

    /**
     * Adjustment operation
     */
    ADJUSTMENT,

    /**
     * Damage/Write-off
     */
    DAMAGE,

    /**
     * Production/Manufacturing
     */
    PRODUCTION,

    /**
     * Internal transfer
     */
    TRANSFER
}
