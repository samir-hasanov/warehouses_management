package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Warehouse;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    
    Optional<Warehouse> findByCode(String code);
    
    Optional<Warehouse> findByCodeAndIsActiveTrue(String code);
    
    List<Warehouse> findByIsActiveTrue();
    
    @Query("SELECT w FROM Warehouse w WHERE w.code = :code OR w.name LIKE %:searchTerm%")
    List<Warehouse> searchWarehouses(@Param("code") String code, @Param("searchTerm") String searchTerm);
    
    boolean existsByCode(String code);
}
