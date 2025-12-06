package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    Optional<Stock> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
    
    List<Stock> findByWarehouseId(Long warehouseId);
    
    List<Stock> findByProductId(Long productId);
    
    @Query("SELECT s FROM Stock s WHERE s.warehouse.id = :warehouseId AND s.quantity < s.minStockLevel")
    List<Stock> findLowStockItems(@Param("warehouseId") Long warehouseId);
    
    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity + :quantity, " +
           "s.availableQuantity = s.availableQuantity + :quantity " +
           "WHERE s.id = :stockId")
    void increaseStock(@Param("stockId") Long stockId, @Param("quantity") BigDecimal quantity);
    
    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity - :quantity, " +
           "s.availableQuantity = s.availableQuantity - :quantity " +
           "WHERE s.id = :stockId")
    void decreaseStock(@Param("stockId") Long stockId, @Param("quantity") BigDecimal quantity);
}
