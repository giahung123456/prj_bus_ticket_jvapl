package com.example.bus_ticket_prj_jvapl.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByRouteDTO {
    private String routeName;
    private Long ticketCount;
    private Double totalRevenue;
}