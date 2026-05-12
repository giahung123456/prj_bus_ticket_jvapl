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


    @Transactional(rollbackFor = Exception.class)
    public Ticket createBooking(BookingRequestDTO dto) {
        // 1. Lấy ghế và kiểm tra (CHỈ KIỂM TRA, KHÔNG SET STATUS VỘI)
        Seat seat = seatRepository.findByIdWithLock(dto.getSeatId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế"));
// IN RA ĐỂ NHÌN TẬN MẮT
        System.err.println("--- KIỂM TRA TRẠNG THÁI THỰC TẾ ---");
        System.err.println("Mã ghế: " + seat.getSeatNumber());
        System.err.println("Trạng thái lấy từ DB: [" + seat.getStatus() + "]");
        System.err.println("So sánh với AVAILABLE: " + (seat.getStatus() == SeatStatus.AVAILABLE));
//        if (seat.getStatus() != SeatStatus.AVAILABLE) {
//            throw new RuntimeException("Ghế này đã có người khác đang thao tác!");
//        }
// SỬA TẠM DÒNG NÀY ĐỂ DEBUG
        if (seat.getStatus() == SeatStatus.BOOKED) { // Chỉ chặn nếu đã bán hẳn
            throw new RuntimeException("Ghế này đã bán!");
        }
        Trip trip = tripRepository.findById(dto.getTripId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi"));

        // 2. Tạo đối tượng Ticket
        Ticket ticket = Ticket.builder()
                .ticketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customerName(dto.getCustomerName())
                .customerPhone(dto.getCustomerPhone())
                .trip(trip)
                .seat(seat)
                .totalPrice(trip.getPrice())
                .status(TicketStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // 3. QUAN TRỌNG: LƯU VÉ TRƯỚC
        // Nếu dòng này lỗi, nó sẽ văng Exception ngay lập tức và không chạy dòng dưới
        Ticket savedTicket = ticketRepository.save(ticket);

        // 4. LƯU VÉ XONG MỚI ĐỔI TRẠNG THÁI GHẾ
        seat.setStatus(SeatStatus.PENDING);
        seatRepository.save(seat);

        return savedTicket;
    }
}