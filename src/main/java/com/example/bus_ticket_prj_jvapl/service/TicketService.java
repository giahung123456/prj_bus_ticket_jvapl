package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.model.dto.TicketDetailDTO;
import com.example.bus_ticket_prj_jvapl.model.entity.Seat;
import com.example.bus_ticket_prj_jvapl.model.entity.Ticket;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.SeatStatus;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus;
import com.example.bus_ticket_prj_jvapl.repository.SeatRepository;
import com.example.bus_ticket_prj_jvapl.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private SeatRepository seatRepository;
    public TicketDetailDTO getTicketDetail(String code, String phone) {
        return ticketRepository.findTicketDetail(code, phone)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin vé phù hợp!"));
    }
    // Trong TicketService.java (hoặc tạo CancelService tùy bạn)
    @Transactional
    public void cancelTicketProactive(String ticketCode, String phone) {
        // 1. Tìm vé
        Ticket ticket = ticketRepository.findByTicketCodeAndPhone(ticketCode, phone)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin vé!"));

        // 2. Kiểm tra trạng thái vé (Chỉ cho phép hủy nếu chưa thanh toán hoặc tùy chính sách)
        if (ticket.getStatus() == TicketStatus.CANCELLED) {
            throw new RuntimeException("Vé này đã được hủy trước đó.");
        }

        // 3. Kiểm tra điều kiện thời gian (Trước 12h khởi hành)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureTime = ticket.getTrip().getDepartureTime();

        if (now.plusHours(12).isAfter(departureTime)) {
            throw new RuntimeException("Chỉ được phép hủy vé trước giờ khởi hành tối thiểu 12 tiếng.");
        }

        // 4. Thực hiện cập nhật
        // Đổi trạng thái vé
        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        // Giải phóng ghế
        Seat seat = ticket.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.save(seat);
    }
    public Double calculateMonthlyRevenue() {
        // Gọi hàm từ Repository (Hàm này bạn đã viết ở bước Hướng 4 trước đó)
        // Nếu chưa viết, hãy đảm bảo TicketRepository có hàm tính tổng tiền theo tháng
        Double revenue = ticketRepository.getTotalRevenueThisMonth();
        return (revenue != null) ? revenue : 0.0;
    }
}