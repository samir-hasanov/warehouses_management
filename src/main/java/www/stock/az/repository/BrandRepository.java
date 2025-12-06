package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Brand;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    
    Optional<Brand> findByCode(String code);
    
    Optional<Brand> findByCodeAndIsActiveTrue(String code);
    
    List<Brand> findByIsActiveTrue();
    
    @Query("SELECT b FROM Brand b WHERE b.isActive = true AND " +
           "(b.code LIKE %:searchTerm% OR b.name LIKE %:searchTerm%)")
    List<Brand> searchBrands(@Param("searchTerm") String searchTerm);
    
    boolean existsByCode(String code);
}
