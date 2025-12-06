package www.stock.az.service;

import www.stock.az.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {
    List<WarehouseResponse> findAllActive();

    WarehouseResponse findById(Long id);

    WarehouseResponse findByCode(String code);
}
