package com.example.bus_ticket_prj_jvapl.model.dto;

import com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TicketDetailDTO {
    private String ticketCode;
    private String customerName;
    private String customerPhone;
    private String seatNumber;
    private String busPlateNumber; // Biển số xe
    private String departureLocation;
    private String destinationLocation;
    private LocalDateTime departureTime;
    private String status;
    private Double totalPrice;
    // PHẢI CÓ Constructor này để khớp với câu SELECT của JPQL
    public TicketDetailDTO(String ticketCode, String customerName, String customerPhone,
                           String seatNumber, String busPlateNumber, String departureLocation,
                           String destinationLocation, LocalDateTime departureTime,
                           TicketStatus status, Double totalPrice) {
        this.ticketCode = ticketCode;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.seatNumber = seatNumber;
        this.busPlateNumber = busPlateNumber;
        this.departureLocation = departureLocation;
        this.destinationLocation = destinationLocation;
        this.departureTime = departureTime;
        this.status = (status != null) ? status.name() : ""; // Ép Enum về String
        this.totalPrice = totalPrice;
    }
}