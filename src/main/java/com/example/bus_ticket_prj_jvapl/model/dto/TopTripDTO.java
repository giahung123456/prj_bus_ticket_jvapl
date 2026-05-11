package com.example.bus_ticket_prj_jvapl.model.dto;

/**
 * Interface Projection để hứng dữ liệu Top chuyến xe từ Repository
 */
public interface TopTripDTO {
    Long getTripId();
    String getRouteName();
    // Lưu ý: Nếu trong DB là kiểu dữ liệu thời gian, Spring sẽ tự map vào String hoặc LocalDateTime
    String getDepartureTime();
    Long getBookingCount();
}