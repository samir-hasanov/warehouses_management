package www.stock.az.service;

import www.stock.az.dto.request.StockInRequest;
import www.stock.az.dto.response.StockMovementResponse;

public interface StockMovementService {
    StockMovementResponse addStockByBarcode(StockInRequest request);

    StockMovementResponse findById(Long id);

    StockMovementResponse findByNumber(String number);
}
