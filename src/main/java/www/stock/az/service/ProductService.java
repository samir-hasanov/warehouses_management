package www.stock.az.service;

import org.springframework.data.domain.Pageable;
import www.stock.az.dto.request.ProductCreateRequest;
import www.stock.az.dto.response.PageResponse;
import www.stock.az.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

PageResponse<ProductResponse> findAllActive(Pageable pageable);

    ProductResponse findById(Long id);

    ProductResponse findByCode(String code);

    ProductResponse findByBarcode(String barcode);

    ProductResponse create(ProductCreateRequest request);
}
