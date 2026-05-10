package com.example.bus_ticket_prj_jvapl.model.dto;

import lombok.*;


@Data

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingRequestDTO {
    private Long tripId;
    private Long seatId;
    private String customerName;
    private String customerPhone;
}