package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.StockMovement;
import www.stock.az.enums.MovementStatus;
import www.stock.az.enums.MovementType;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    Optional<StockMovement> findByMovementNumber(String movementNumber);
    
    List<StockMovement> findByMovementType(MovementType movementType);
    
    List<StockMovement> findByStatus(MovementStatus status);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.targetWarehouse.id = :warehouseId")
    List<StockMovement> findByTargetWarehouse(@Param("warehouseId") Long warehouseId);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.sourceWarehouse.id = :warehouseId")
    List<StockMovement> findBySourceWarehouse(@Param("warehouseId") Long warehouseId);
    
    boolean existsByMovementNumber(String movementNumber);
}
