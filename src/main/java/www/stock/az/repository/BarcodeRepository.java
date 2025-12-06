package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Barcode;
import www.stock.az.entity.Product;

import java.util.Optional;

@Repository
public interface BarcodeRepository extends JpaRepository<Barcode, Long> {
    
    Optional<Barcode> findByBarcode(String barcode);
    
    @Query("SELECT b.product FROM Barcode b WHERE b.barcode = :barcode AND b.product.isActive = true")
    Optional<Product> findProductByBarcode(@Param("barcode") String barcode);
    
    @Query("SELECT b FROM Barcode b WHERE b.barcode = :barcode AND b.isPrimary = true")
    Optional<Barcode> findPrimaryBarcode(@Param("barcode") String barcode);
    
    boolean existsByBarcode(String barcode);
}
