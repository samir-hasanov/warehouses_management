package www.stock.az.service;

import www.stock.az.dto.request.WarehouseCreateRequest;
import www.stock.az.dto.request.WarehouseUpdateRequest;
import www.stock.az.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {
    List<WarehouseResponse> findAllActive();

    WarehouseResponse findById(Long id);

    WarehouseResponse findByCode(String code);

    WarehouseResponse create(WarehouseCreateRequest request);

    WarehouseResponse update(Long id, WarehouseUpdateRequest request);

    void delete(Long id);
}
