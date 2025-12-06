package www.stock.az.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * AuditLog entity for tracking all operations and changes
 */
@Entity
@Table(name = "audit_logs", schema = "click_user",
        indexes = {
                @Index(name = "idx_audit_entity_type", columnList = "entity_type,entity_id"),
                @Index(name = "idx_audit_created_at", columnList = "created_at"),
                @Index(name = "idx_audit_user", columnList = "user_name")
        })
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog extends BaseEntity {

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType; // Product, Stock, StockMovement, etc.

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "action", nullable = false, length = 50)
    private String action; // CREATE, UPDATE, DELETE, VIEW, etc.

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "request_method", length = 10)
    private String requestMethod; // GET, POST, PUT, DELETE

    @Column(name = "request_url", length = 500)
    private String requestUrl;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues; // JSON representation of old values

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues; // JSON representation of new values

    @Column(name = "changes", columnDefinition = "TEXT")
    private String changes; // Description of what changed

    @Column(name = "status", length = 50)
    private String status; // SUCCESS, FAILED, ERROR

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs; // Execution time in milliseconds
}
