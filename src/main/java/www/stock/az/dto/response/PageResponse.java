package www.stock.az.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;       // səhifədəki məlumatlar (məs: mehsullarin siyahısı)
    private int pageNumber;        // mehsul səhifə nömrəsi
    private int pageSize;          // səhifənin ölçüsü (məs: 4)
    private long totalElements;    // ümumi elementlərin sayı
    private int totalPages;
}
