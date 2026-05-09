package com.example.bus_ticket_prj_jvapl.repository;

import com.example.bus_ticket_prj_jvapl.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Tìm danh sách vé của chuyến để check xem ghế nào đã được bán
    List<Ticket> findByTripId(Long tripId);

    // Dùng để kiểm tra nhanh trước khi lưu (Conflict Check)
    boolean existsByTripIdAndSeatId(Long tripId, Long seatId);
}