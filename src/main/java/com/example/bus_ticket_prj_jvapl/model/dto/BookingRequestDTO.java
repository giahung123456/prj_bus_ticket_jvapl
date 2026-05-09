package com.example.bus_ticket_prj_jvapl.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequestDTO {
    private Long tripId;
    private Long seatId;
    private String customerName;
    private String customerPhone;
}