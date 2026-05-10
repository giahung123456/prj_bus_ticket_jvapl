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
//    @Transactional(rollbackFor = Exception.class)
//    public Ticket createBooking(BookingRequestDTO dto) {
//
//        // 1. SELECT seat ... FOR UPDATE (Khóa dòng này để tránh tranh chấp)
//        // Sử dụng phương thức có Lock mà mình đã thêm vào Repository
//        Seat seat = seatRepository.findByIdWithLock(dto.getSeatId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế"));
//
//        // 2. Kiểm tra trạng thái ghế ngay sau khi khóa
//        if (seat.getStatus() != SeatStatus.AVAILABLE) {
//            throw new RuntimeException("Rất tiếc, ghế " + seat.getSeatNumber() + " đã có người khác nhanh tay đặt trước!");
//        }
//
//        // 3. Tìm chuyến đi
//        Trip trip = tripRepository.findById(dto.getTripId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi"));
//
//        // 4. Tạo Ticket (INSERT)
//        Ticket ticket = Ticket.builder()
//                .ticketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
//                .customerName(dto.getCustomerName())
//                .customerPhone(dto.getCustomerPhone())
//                .trip(trip)
//                .seat(seat) // Gán trực tiếp Object Seat vào Ticket
//                .totalPrice(trip.getPrice())
//                .status(TicketStatus.PENDING)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        Ticket savedTicket = ticketRepository.save(ticket);
//
//        // 5. Cập nhật trạng thái ghế (UPDATE)
//        seat.setStatus(SeatStatus.PENDING);
//        seatRepository.save(seat);
//
//        // Trả về ticket đã lưu (Spring sẽ tự COMMIT tại đây)
//        return savedTicket;
//    }
//    @Transactional(rollbackFor = Exception.class)
//    public Ticket createBooking(BookingRequestDTO dto) {
//        // 1. Khóa dòng dữ liệu ghế
//        Seat seat = seatRepository.findByIdWithLock(dto.getSeatId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế"));
//
//        // 2. Kiểm tra trạng thái ghế (SeatStatus)
//        // Nếu ghế đã có người đặt chính thức (BOOKED) thì mới chặn
//        if (seat.getStatus() == SeatStatus.BOOKED) {
//            throw new RuntimeException("Rất tiếc, ghế " + seat.getSeatNumber() + " đã có người đặt!");
//        }
//
//        // 3. Lấy thông tin chuyến đi
//        Trip trip = tripRepository.findById(dto.getTripId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi"));
//
//        // 4. Tạo thực thể Ticket và dùng TicketStatus.BOOKED (đã thêm ở Bước 1)
//        Ticket ticket = Ticket.builder()
//                .ticketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
//                .customerName(dto.getCustomerName())
//                .customerPhone(dto.getCustomerPhone())
//                .trip(trip)
//                .seat(seat)
//                .totalPrice(trip.getPrice())
//                .status(TicketStatus.BOOKED) // Sử dụng giá trị vừa thêm vào Enum
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        Ticket savedTicket = ticketRepository.save(ticket);
//
//        // 5. Cập nhật trạng thái Ghế thành BOOKED để người sau không chọn được nữa
//        seat.setStatus(SeatStatus.BOOKED);
//        seatRepository.save(seat);
//
//        return savedTicket;
//    }
//    @Transactional(rollbackFor = Exception.class)
//    public Ticket createBooking(BookingRequestDTO dto) {
//        Seat seat = seatRepository.findByIdWithLock(dto.getSeatId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế"));
//
////        // Kiểm tra: Nếu ghế đã có người đặt (BOOKED) hoặc đang chờ duyệt (PENDING) thì không cho đặt nữa
////        if (seat.getStatus() != SeatStatus.AVAILABLE) {
////            throw new RuntimeException("Ghế " + seat.getSeatNumber() + " không còn trống!");
////        }
//        System.err.println("Trạng thái ghế hiện tại: " + seat.getStatus());
//        if (!SeatStatus.AVAILABLE.equals(seat.getStatus())) {
//            throw new RuntimeException("Lỗi: Ghế " + seat.getSeatNumber() + " đang ở trạng thái " + seat.getStatus());
//        }
//        Trip trip = tripRepository.findById(dto.getTripId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi"));
//
//        Ticket ticket = Ticket.builder()
//                .ticketCode("TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
//                .customerName(dto.getCustomerName())
//                .customerPhone(dto.getCustomerPhone())
//                .trip(trip)
//                .seat(seat)
//                .totalPrice(trip.getPrice())
//                .status(TicketStatus.PENDING) // Sửa thành PENDING để Staff thấy
//                .createdAt(LocalDateTime.now())
//                .build();
//
////        Ticket savedTicket = ticketRepository.save(ticket);
////
////        // Cập nhật trạng thái ghế thành PENDING để hiển thị màu "đang chờ"
////        seat.setStatus(SeatStatus.PENDING);
////        seatRepository.save(seat);
//// Trong BookingService.java
//        try {
//            Ticket savedTicket = ticketRepository.save(ticket);
//
//            // Chỉ cập nhật ghế sau khi vé đã lưu thành công
//            seat.setStatus(SeatStatus.PENDING);
//            seatRepository.save(seat);
//
//            return savedTicket;
//        } catch (Exception e) {
//            System.err.println("LỖI SQL KHI LƯU VÉ: " + e.getMessage());
//            throw e; // Ném ra để Controller bắt
//        }
//
//    }
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