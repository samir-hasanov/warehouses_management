package www.stock.az.enums;

/**
 * Excel: Təsdiqləndi, Təsdiq gözləyir, Düzəlişə qaytarıldı, Sistem tərəfindən təsdiqləndi
 */
public enum InvoiceStatus {
    DRAFT,                          // Qaralama
    PENDING_CONFIRMATION,           // Təsdiq gözləyir
    APPROVED,                       // Təsdiqləndi
    RETURNED_FOR_CORRECTION,        // Düzəlişə qaytarıldı
    CONFIRMED_BY_SYSTEM,            // Sistem tərəfindən təsdiqləndi
    CANCELLED                       // Ləğv
}

