package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.BrandCreateRequest;
import www.stock.az.dto.request.BrandUpdateRequest;
import www.stock.az.dto.response.BrandResponse;
import www.stock.az.entity.Brand;
import www.stock.az.repository.BrandRepository;
import www.stock.az.service.BrandService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandServiceImpl implements BrandService {
    
    private final BrandRepository brandRepository;
    
    public List<BrandResponse> findAllActive() {
        return brandRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public BrandResponse findById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brend tapılmadı: " + id));
        return mapToResponse(brand);
    }
    
    public BrandResponse findByCode(String code) {
        Brand brand = brandRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Brend tapılmadı: " + code));
        return mapToResponse(brand);
    }
    
    public List<BrandResponse> search(String searchTerm) {
        return brandRepository.searchBrands(searchTerm)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BrandResponse create(BrandCreateRequest request) {
        String code = (request.getCode() != null && !request.getCode().isBlank())
                ? request.getCode().trim().toUpperCase()
                : ("BR-" + System.currentTimeMillis());
        if (brandRepository.existsByCode(code)) {
            throw new RuntimeException("Bu kod ilə brand artıq mövcuddur: " + code);
        }

        Brand brand = new Brand();
        brand.setCode(code);
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setCountry(request.getCountry());
        brand.setWebsite(request.getWebsite());
        brand.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Brand savedBrand = brandRepository.save(brand);
        return mapToResponse(savedBrand);
    }

    @Override
    public BrandResponse update(Long id, BrandUpdateRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand tapılmadı: " + id));

        brand.setName(request.getName());
        if (request.getDescription() != null) {
            brand.setDescription(request.getDescription());
        }
        if (request.getCountry() != null) {
            brand.setCountry(request.getCountry());
        }
        if (request.getWebsite() != null) {
            brand.setWebsite(request.getWebsite());
        }
        if (request.getIsActive() != null) {
            brand.setIsActive(request.getIsActive());
        }

        Brand updatedBrand = brandRepository.save(brand);
        return mapToResponse(updatedBrand);
    }

    @Override
    public void delete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand tapılmadı: " + id));

        // Soft delete - set isActive to false instead of hard delete
        brand.setIsActive(false);
        brandRepository.save(brand);
    }
    
    private BrandResponse mapToResponse(Brand brand) {
        BrandResponse response = new BrandResponse();
        response.setId(brand.getId());
        response.setCode(brand.getCode());
        response.setName(brand.getName());
        response.setDescription(brand.getDescription());
        response.setCountry(brand.getCountry());
        response.setWebsite(brand.getWebsite());
        response.setIsActive(brand.getIsActive());
        response.setCreatedAt(brand.getCreatedAt());
        response.setUpdatedAt(brand.getUpdatedAt());
        return response;
    }
}
