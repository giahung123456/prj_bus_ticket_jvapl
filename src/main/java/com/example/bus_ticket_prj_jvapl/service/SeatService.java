package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.model.dto.SeatMapDTO;
import com.example.bus_ticket_prj_jvapl.model.entity.Seat;
import com.example.bus_ticket_prj_jvapl.model.entity.Ticket;
import com.example.bus_ticket_prj_jvapl.repository.SeatRepository;
import com.example.bus_ticket_prj_jvapl.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Service
public class SeatService {
    @Autowired private SeatRepository seatRepository;
    @Autowired private TicketRepository ticketRepository;
    
    public List<SeatMapDTO> getSeatMapForTrip(Long tripId) {
        // 1. Lấy tất cả ghế của xe thuộc chuyến đi này
        List<Seat> allSeats = seatRepository.findByTripId(tripId);

        // 2. Lấy danh sách vé đã tồn tại của chuyến đi này (để biết ghế nào bận)
        List<Ticket> activeTickets = ticketRepository.findByTripId(tripId);

        return allSeats.stream().map(seat -> {
            // Kiểm tra xem ghế này đã có vé chưa (PENDING hoặc BOOKED)
            String status = activeTickets.stream()
                    .filter(t -> t.getSeat().getId().equals(seat.getId()))
                    .map(t -> t.getStatus().name())
                    .findFirst()
                    .orElse("AVAILABLE");

            return new SeatMapDTO(seat.getId(), seat.getSeatNumber(), status);
        }).collect(Collectors.toList());
    }
}