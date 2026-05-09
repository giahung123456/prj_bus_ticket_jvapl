package com.example.bus_ticket_prj_jvapl.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatMapDTO {
    private Long id;
    private String seatNumber;
    private String status; // AVAILABLE, PENDING, BOOKED
}