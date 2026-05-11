package com.example.bus_ticket_prj_jvapl.model.dto;

/**
 * Interface Projection để hứng dữ liệu thống kê doanh thu từ Repository
 */
public interface RevenueByRouteDTO {
    String getRouteName();
    Long getTicketCount();
    Double getTotalRevenue();
}