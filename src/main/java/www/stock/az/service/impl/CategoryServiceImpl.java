package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.CategoryCreateRequest;
import www.stock.az.dto.request.CategoryUpdateRequest;
import www.stock.az.dto.response.CategoryResponse;
import www.stock.az.entity.Category;
import www.stock.az.repository.CategoryRepository;
import www.stock.az.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public List<CategoryResponse> findAllActive() {
        return categoryRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı: " + id));
        return mapToResponse(category);
    }
    
    public CategoryResponse findByCode(String code) {
        Category category = categoryRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı: " + code));
        return mapToResponse(category);
    }
    
    public List<CategoryResponse> search(String searchTerm) {
        return categoryRepository.searchCategories(searchTerm)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse create(CategoryCreateRequest request) {
        String code = (request.getCode() != null && !request.getCode().isBlank())
                ? request.getCode().trim().toUpperCase()
                : ("CAT-" + System.currentTimeMillis());
        if (categoryRepository.existsByCode(code)) {
            throw new RuntimeException("Bu kod ilə kateqoriya artıq mövcuddur: " + code);
        }

        // Validate parent category if provided
        if (request.getParentId() != null) {
            categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Ana kateqoriya tapılmadı: " + request.getParentId()));
        }

        Category category = new Category();
        category.setCode(code);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());
        category.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı: " + id));

        // Prevent circular reference - category cannot be its own parent
        if (request.getParentId() != null && request.getParentId().equals(id)) {
            throw new RuntimeException("Kateqoriya özünün ana kateqoriyası ola bilməz");
        }

        // Validate parent category if provided
        if (request.getParentId() != null) {
            categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Ana kateqoriya tapılmadı: " + request.getParentId()));
        }

        category.setName(request.getName());
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getParentId() != null) {
            category.setParentId(request.getParentId());
        }
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kateqoriya tapılmadı: " + id));

        // Check if category has child categories
        List<Category> childCategories = categoryRepository.findByParentId(id);
        if (!childCategories.isEmpty()) {
            throw new RuntimeException("Bu kateqoriyanın alt kateqoriyaları var. Əvvəlcə onları silin və ya başqa kateqoriyaya köçürün.");
        }

        // Soft delete - set isActive to false instead of hard delete
        category.setIsActive(false);
        categoryRepository.save(category);
    }
    
    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setCode(category.getCode());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setParentId(category.getParentId());
        response.setIsActive(category.getIsActive());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}
