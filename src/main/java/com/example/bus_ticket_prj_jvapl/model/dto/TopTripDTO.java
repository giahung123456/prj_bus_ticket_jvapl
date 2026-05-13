package com.example.bus_ticket_prj_jvapl.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TopTripDTO {
    private Long tripId;
    private String routeName;
    private LocalDateTime departureTime;
    private Long bookingCount;
    private String plateNumber; // Thêm mới
    private String busType;
}