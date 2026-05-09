package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.model.dto.BookingRequestDTO;
import com.example.bus_ticket_prj_jvapl.model.entity.Seat;
import com.example.bus_ticket_prj_jvapl.model.entity.Ticket;
import com.example.bus_ticket_prj_jvapl.model.entity.Trip;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.SeatStatus;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus;
import com.example.bus_ticket_prj_jvapl.repository.SeatRepository;
import com.example.bus_ticket_prj_jvapl.repository.TicketRepository;
import com.example.bus_ticket_prj_jvapl.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired private TicketRepository ticketRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private TripRepository tripRepository;

    /**
     * CORE-06: Đặt vé & tính toàn vẹn dữ liệu
     * Sử dụng Pessimistic Lock để chống đặt trùng ghế
     */
    @Transactional(rollbackFor = Exception.class)
    public Ticket createBooking(BookingRequestDTO dto) {

        // 1. SELECT seat ... FOR UPDATE (Khóa dòng này để tránh tranh chấp)
        // Sử dụng phương thức có Lock mà mình đã thêm vào Repository
        Seat seat = seatRepository.findByIdWithLock(dto.getSeatId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế"));

        // 2. Kiểm tra trạng thái ghế ngay sau khi khóa
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new RuntimeException("Rất tiếc, ghế " + seat.getSeatNumber() + " đã có người khác nhanh tay đặt trước!");
        }

        // 3. Tìm chuyến đi
        Trip trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi"));

        // 4. Tạo Ticket (INSERT)
        Ticket ticket = Ticket.builder()
                .ticketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customerName(dto.getCustomerName())
                .customerPhone(dto.getCustomerPhone())
                .trip(trip)
                .seat(seat) // Gán trực tiếp Object Seat vào Ticket
                .totalPrice(trip.getPrice())
                .status(TicketStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        // 5. Cập nhật trạng thái ghế (UPDATE)
        seat.setStatus(SeatStatus.PENDING);
        seatRepository.save(seat);

        // Trả về ticket đã lưu (Spring sẽ tự COMMIT tại đây)
        return savedTicket;
    }
}