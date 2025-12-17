package www.stock.az.service;

import www.stock.az.dto.request.StockInRequest;
import www.stock.az.dto.request.StockMovementCreateRequest;
import www.stock.az.dto.response.StockMovementResponse;
import www.stock.az.enums.MovementType;

import java.util.List;

public interface StockMovementService {
    StockMovementResponse addStockByBarcode(StockInRequest request);

    StockMovementResponse create(StockMovementCreateRequest request);

    List<StockMovementResponse> findAll(MovementType type);

    StockMovementResponse findById(Long id);

    StockMovementResponse findByNumber(String number);
    
    StockMovementResponse approve(Long id, String approvedBy);
    
    StockMovementResponse cancel(Long id, String reason);
}
