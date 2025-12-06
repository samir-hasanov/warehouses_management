package www.stock.az.service;

import www.stock.az.dto.request.BrandCreateRequest;
import www.stock.az.dto.request.BrandUpdateRequest;
import www.stock.az.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
    List<BrandResponse> findAllActive();

    BrandResponse findById(Long id);

    BrandResponse findByCode(String code);

    List<BrandResponse> search(String q);

    BrandResponse create(BrandCreateRequest request);

    BrandResponse update(Long id, BrandUpdateRequest request);

    void delete(Long id);
}
