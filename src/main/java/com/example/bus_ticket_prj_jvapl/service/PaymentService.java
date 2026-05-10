package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.model.entity.Ticket;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.SeatStatus;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus;
import com.example.bus_ticket_prj_jvapl.repository.SeatRepository;
import com.example.bus_ticket_prj_jvapl.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    @Autowired private TicketRepository ticketRepository;
    @Autowired private SeatRepository seatRepository;

    // Lấy danh sách các vé đang chờ xử lý
    public List<Ticket> getPendingTickets() {
        return ticketRepository.findByStatus(TicketStatus.PENDING);
    }

    @Transactional
    public void confirmPayment(Long ticketId) {
        // 1. SELECT FOR UPDATE để tránh xung đột dữ liệu
        Ticket ticket = ticketRepository.findByIdWithLock(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));

        if (ticket.getStatus() != TicketStatus.PENDING) {
            throw new RuntimeException("Vé đã được xử lý trước đó!");
        }

        // 2. Cập nhật trạng thái vé thành PAID
        ticket.setStatus(TicketStatus.PAID);
        ticketRepository.save(ticket);

        // 3. Cập nhật trạng thái ghế thành BOOKED (chính thức có chủ)
        ticket.getSeat().setStatus(SeatStatus.BOOKED);
        seatRepository.save(ticket.getSeat());
    }

    @Transactional
    public void cancelTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vé"));

        // Cập nhật vé thành CANCELLED
        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        // Giải phóng ghế thành AVAILABLE để người khác đặt
        ticket.getSeat().setStatus(SeatStatus.AVAILABLE);
        seatRepository.save(ticket.getSeat());
    }
}