package www.stock.az.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import www.stock.az.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByCode(String code);
    
    Optional<Category> findByCodeAndIsActiveTrue(String code);
    
    List<Category> findByIsActiveTrue();
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND " +
           "(c.code LIKE %:searchTerm% OR c.name LIKE %:searchTerm%)")
    List<Category> searchCategories(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.parentId = :parentId")
    List<Category> findByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true AND c.parentId IS NULL")
    List<Category> findRootCategories();
    
    boolean existsByCode(String code);
}
