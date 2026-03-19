package www.stock.az.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.stock.az.dto.request.InvoiceCreateRequest;
import www.stock.az.dto.request.InvoiceLineRequest;
import www.stock.az.dto.response.CounterpartyResponse;
import www.stock.az.dto.response.InvoiceLineResponse;
import www.stock.az.dto.response.InvoiceResponse;
import www.stock.az.dto.response.WarehouseResponse;
import www.stock.az.entity.*;
import www.stock.az.enums.CounterpartyType;
import www.stock.az.enums.InvoiceDirection;
import www.stock.az.enums.InvoiceStatus;
import www.stock.az.repository.*;
import www.stock.az.service.StockMovementService;
import www.stock.az.dto.request.StockMovementCreateRequest;
import www.stock.az.enums.MovementType;
import www.stock.az.service.InvoiceService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final StockMovementService stockMovementService;

    @Override
    public InvoiceResponse create(InvoiceCreateRequest request) {
        Counterparty seller = findOrCreateCounterparty(request.getSellerVoen(), request.getSellerName(), CounterpartyType.SUPPLIER);
        Counterparty buyer = findOrCreateCounterparty(request.getBuyerVoen(), request.getBuyerName(), CounterpartyType.CUSTOMER);

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Anbar tapılmadı: " + request.getWarehouseId()));

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(request.getInvoiceNumber());
        invoice.setDirection(request.getDirection());
        invoice.setInvoiceType(request.getInvoiceType());
        invoice.setStatus(InvoiceStatus.APPROVED);
        invoice.setIssueDate(request.getIssueDate());
        invoice.setSeller(seller);
        invoice.setBuyer(buyer);
        invoice.setWarehouse(warehouse);
        invoice.setCurrency(request.getCurrency());
        invoice.setNotes(request.getNotes());

        List<InvoiceLine> lines = new ArrayList<>();
        BigDecimal totalNet = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;
        BigDecimal totalGross = BigDecimal.ZERO;

        for (InvoiceLineRequest lineRequest : request.getLines()) {
            Product product = null;
            String code = lineRequest.getProductCode();
            String name = lineRequest.getProductName();

            // Əvvəlcə frontend-dən gələn productId ilə axtar
            if (lineRequest.getProductId() != null) {
                product = productRepository.findById(lineRequest.getProductId()).orElse(null);
            }
            // Əgər productId boşdursa və ya tapılmadısa, Kod/barcode ilə mövcud məhsulu axtar
            if (product == null && code != null && !code.isBlank()) {
                product = productRepository.findByCodeAndIsActiveTrue(code.trim()).orElse(null);
            }

            // Əgər hələ də məhsul tapılmayıbsa, qaimə sətirindən avtomatik məhsul yarat
            if (product == null && code != null && !code.isBlank()) {
                Product autoProduct = new Product();
                autoProduct.setCode(code.trim());
                autoProduct.setName((name != null && !name.isBlank()) ? name.trim() : code.trim());
                autoProduct.setUnit(lineRequest.getUnit() != null && !lineRequest.getUnit().isBlank() ? lineRequest.getUnit().trim() : "ədəd");
                autoProduct.setIsActive(true);

                // Default kateqoriya
                Category category = categoryRepository.findByCode("AUTO")
                        .orElseGet(() -> {
                            Category c = new Category();
                            c.setCode("AUTO");
                            c.setName("Avtomatik kateqoriya");
                            c.setIsActive(true);
                            return categoryRepository.save(c);
                        });
                autoProduct.setCategory(category);

                // Default brend
                Brand brand = brandRepository.findByCode("AUTO")
                        .orElseGet(() -> {
                            Brand b = new Brand();
                            b.setCode("AUTO");
                            b.setName("Avtomatik brend");
                            b.setIsActive(true);
                            return brandRepository.save(b);
                        });
                autoProduct.setBrand(brand);

                product = productRepository.save(autoProduct);
            }

            if (product != null) {
                if (code == null || code.isBlank()) {
                    code = product.getCode();
                }
                if (name == null || name.isBlank()) {
                    name = product.getName();
                }
            }

            InvoiceLine line = new InvoiceLine();
            line.setInvoice(invoice);
            line.setProduct(product);
            line.setProductCode(code);
            line.setProductName(name);
            line.setUnit(lineRequest.getUnit());
            line.setCategoryName(lineRequest.getCategoryName());
            line.setStockNumber(lineRequest.getStockNumber());
            line.setManufacturer(lineRequest.getManufacturer());
            line.setBatchNumber(lineRequest.getBatchNumber());
            line.setQuantity(lineRequest.getQuantity());
            line.setUnitPrice(lineRequest.getUnitPrice());

            BigDecimal lineNet = lineRequest.getUnitPrice().multiply(lineRequest.getQuantity());
            BigDecimal lineVat = lineRequest.getVatAmount() != null ? lineRequest.getVatAmount() : BigDecimal.ZERO;
            BigDecimal lineGross = lineNet.add(lineVat);

            line.setAmountNet(lineNet);
            line.setVatAmount(lineVat);
            line.setAmountGross(lineGross);

            totalNet = totalNet.add(lineNet);
            totalVat = totalVat.add(lineVat);
            totalGross = totalGross.add(lineGross);

            lines.add(line);
        }

        invoice.setTotalNet(totalNet);
        invoice.setTotalVat(totalVat);
        invoice.setTotalGross(totalGross);
        invoice.setLines(lines);

        Invoice saved = invoiceRepository.save(invoice);
        invoiceLineRepository.saveAll(lines);

        // Gələn qaimələr üçün (IN) avtomatik STOCK_IN hərəkəti yaradıb stoku artırırıq
        if (saved.getDirection() == InvoiceDirection.IN) {
            StockMovementCreateRequest movementRequest = new StockMovementCreateRequest();
            movementRequest.setMovementType(MovementType.STOCK_IN);
            movementRequest.setTargetWarehouseId(saved.getWarehouse().getId());
            movementRequest.setMovementDate(saved.getIssueDate());
            movementRequest.setReferenceNumber(saved.getInvoiceNumber());
            movementRequest.setNotes("Invoice import: " + saved.getInvoiceNumber());

            List<StockMovementCreateRequest.StockMovementItemRequest> movementItems = new ArrayList<>();
            for (InvoiceLine line : lines) {
                if (line.getProduct() == null) {
                    // Məhsul sistemi üzrə məlum deyilsə, stok hərəkəti yarada bilmirik – bu sətri atlayırıq
                    continue;
                }
                StockMovementCreateRequest.StockMovementItemRequest itemRequest =
                        new StockMovementCreateRequest.StockMovementItemRequest(
                                line.getProduct().getId(),
                                line.getQuantity()
                        );
                movementItems.add(itemRequest);
            }

            if (!movementItems.isEmpty()) {
                movementRequest.setItems(movementItems);
                // StockMovementService artıq STOCK_IN üçün stoku dərhal artırır
                stockMovementService.create(movementRequest);
            }
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse findById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Qaimə tapılmadı: " + id));
        return mapToResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse findByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Qaimə tapılmadı: " + invoiceNumber));
        return mapToResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> search(InvoiceDirection direction, LocalDateTime fromDate, LocalDateTime toDate) {
        Specification<Invoice> spec = Specification.where(null);

        if (direction != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("direction"), direction));
        }
        if (fromDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("issueDate"), fromDate));
        }
        if (toDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("issueDate"), toDate));
        }

        return invoiceRepository.findAll(spec, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "issueDate"))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Counterparty findOrCreateCounterparty(String voen, String name, CounterpartyType defaultType) {
        return counterpartyRepository.findByVoen(voen)
                .map(existing -> {
                    if (existing.getName() == null || existing.getName().isBlank()) {
                        existing.setName(name);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    Counterparty counterparty = new Counterparty();
                    counterparty.setVoen(voen);
                    counterparty.setName(name);
                    counterparty.setType(defaultType);
                    return counterpartyRepository.save(counterparty);
                });
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setDirection(invoice.getDirection());
        response.setStatus(invoice.getStatus() != null ? invoice.getStatus() : InvoiceStatus.DRAFT);
        response.setInvoiceType(invoice.getInvoiceType());
        response.setIssueDate(invoice.getIssueDate());
        response.setCurrency(invoice.getCurrency());
        response.setNotes(invoice.getNotes());
        response.setTotalNet(invoice.getTotalNet());
        response.setTotalVat(invoice.getTotalVat());
        response.setTotalGross(invoice.getTotalGross());

        if (invoice.getSeller() != null) {
            response.setSeller(mapCounterparty(invoice.getSeller()));
        }
        if (invoice.getBuyer() != null) {
            response.setBuyer(mapCounterparty(invoice.getBuyer()));
        }
        if (invoice.getWarehouse() != null) {
            WarehouseResponse warehouseResponse = new WarehouseResponse();
            warehouseResponse.setId(invoice.getWarehouse().getId());
            warehouseResponse.setCode(invoice.getWarehouse().getCode());
            warehouseResponse.setName(invoice.getWarehouse().getName());
            response.setWarehouse(warehouseResponse);
        }

        if (invoice.getLines() != null) {
            response.setLines(invoice.getLines().stream()
                    .map(this::mapLine)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private CounterpartyResponse mapCounterparty(Counterparty counterparty) {
        CounterpartyResponse response = new CounterpartyResponse();
        response.setId(counterparty.getId());
        response.setVoen(counterparty.getVoen());
        response.setName(counterparty.getName());
        response.setType(counterparty.getType());
        response.setPhone(counterparty.getPhone());
        response.setAddress(counterparty.getAddress());
        return response;
    }

    private InvoiceLineResponse mapLine(InvoiceLine line) {
        InvoiceLineResponse response = new InvoiceLineResponse();
        response.setId(line.getId());
        if (line.getProduct() != null) {
            response.setProductId(line.getProduct().getId());
        }
        response.setProductCode(line.getProductCode());
        response.setProductName(line.getProductName());
        response.setUnit(line.getUnit());
        response.setCategoryName(line.getCategoryName());
        response.setStockNumber(line.getStockNumber());
        response.setManufacturer(line.getManufacturer());
        response.setBatchNumber(line.getBatchNumber());
        response.setQuantity(line.getQuantity());
        response.setUnitPrice(line.getUnitPrice());
        response.setAmountNet(line.getAmountNet());
        response.setVatAmount(line.getVatAmount());
        response.setAmountGross(line.getAmountGross());
        return response;
    }
}

