package www.stock.az.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByCode(String code);
    
    Optional<Product> findByCodeAndIsActiveTrue(String code);

    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.code LIKE %:searchTerm% OR p.name LIKE %:searchTerm%")
    List<Product> searchProducts(@Param("searchTerm") String searchTerm);
    
    boolean existsByCode(String code);
}
