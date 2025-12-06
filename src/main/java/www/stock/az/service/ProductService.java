package www.stock.az.service;

import www.stock.az.dto.request.ProductCreateRequest;
import www.stock.az.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> findAllActive();

    ProductResponse findById(Long id);

    ProductResponse findByCode(String code);

    ProductResponse findByBarcode(String barcode);

    ProductResponse create(ProductCreateRequest request);
}
