package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.WarehouseCreateRequest;
import www.stock.az.dto.request.WarehouseUpdateRequest;
import www.stock.az.dto.response.WarehouseResponse;
import www.stock.az.entity.Warehouse;
import www.stock.az.repository.WarehouseRepository;
import www.stock.az.service.WarehouseService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {
    
    private final WarehouseRepository warehouseRepository;
    
    public WarehouseResponse findByCode(String code) {
        Warehouse warehouse = warehouseRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Anbar tapılmadı: " + code));
        return mapToResponse(warehouse);
    }
    
    public WarehouseResponse findById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anbar tapılmadı: " + id));
        return mapToResponse(warehouse);
    }
    
    public List<WarehouseResponse> findAllActive() {
        return warehouseRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseResponse create(WarehouseCreateRequest request) {
        // Check if code already exists
        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Bu kod ilə anbar artıq mövcuddur: " + request.getCode());
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setDescription(request.getDescription());
        warehouse.setAddress(request.getAddress());
        warehouse.setCity(request.getCity());
        warehouse.setCountry(request.getCountry());
        warehouse.setPhone(request.getPhone());
        warehouse.setEmail(request.getEmail());
        warehouse.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        return mapToResponse(savedWarehouse);
    }

    @Override
    public WarehouseResponse update(Long id, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anbar tapılmadı: " + id));

        warehouse.setName(request.getName());
        if (request.getDescription() != null) {
            warehouse.setDescription(request.getDescription());
        }
        if (request.getAddress() != null) {
            warehouse.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            warehouse.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            warehouse.setCountry(request.getCountry());
        }
        if (request.getPhone() != null) {
            warehouse.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            warehouse.setEmail(request.getEmail());
        }
        if (request.getIsActive() != null) {
            warehouse.setIsActive(request.getIsActive());
        }

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return mapToResponse(updatedWarehouse);
    }

    @Override
    public void delete(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anbar tapılmadı: " + id));

        // Soft delete - set isActive to false instead of hard delete
        warehouse.setIsActive(false);
        warehouseRepository.save(warehouse);
    }
    
    private WarehouseResponse mapToResponse(Warehouse warehouse) {
        WarehouseResponse response = new WarehouseResponse();
        response.setId(warehouse.getId());
        response.setCode(warehouse.getCode());
        response.setName(warehouse.getName());
        response.setDescription(warehouse.getDescription());
        response.setAddress(warehouse.getAddress());
        response.setCity(warehouse.getCity());
        response.setCountry(warehouse.getCountry());
        response.setPhone(warehouse.getPhone());
        response.setEmail(warehouse.getEmail());
        response.setIsActive(warehouse.getIsActive());
        response.setCreatedAt(warehouse.getCreatedAt());
        response.setUpdatedAt(warehouse.getUpdatedAt());
        return response;
    }
}
