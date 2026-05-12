package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.entity.Seat;
import com.example.bus_ticket_prj_jvapl.model.entity.Ticket;
import com.example.bus_ticket_prj_jvapl.model.entity.Trip;
import com.example.bus_ticket_prj_jvapl.model.entity.User;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.SeatStatus;
import com.example.bus_ticket_prj_jvapl.repository.SeatRepository;
import com.example.bus_ticket_prj_jvapl.repository.TicketRepository;
import com.example.bus_ticket_prj_jvapl.repository.TripRepository;
import com.example.bus_ticket_prj_jvapl.service.SeatService;
import com.example.bus_ticket_prj_jvapl.service.TicketService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private SeatService seatService;
    @Autowired private TripRepository tripRepository;
    @Autowired private SeatRepository seatRepository;

   // Tiêm Repository vào đây
    // Bước 1: Hiển thị sơ đồ ghế sau khi khách chọn chuyến
    @GetMapping("/select-seat")
    public String showSeatMap(@RequestParam Long tripId, Model model) {
        // Lấy danh sách SeatMapDTO (đã gắn status AVAILABLE/PENDING/BOOKED)
        var seats = seatService.getSeatMapForTrip(tripId);

        model.addAttribute("seats", seats);
        model.addAttribute("trip", tripRepository.findById(tripId).orElse(null));

        return "passenger/select-seat"; // templates/passenger/select-seat.html
    }
    @GetMapping("/search")
    public String searchTrips(@RequestParam String departure,
                              @RequestParam String destination,
                              @RequestParam String date,
                              Model model) {
        try {
            LocalDate travelDate = LocalDate.parse(date);
            List<Trip> trips = tripRepository.findAvailableTrips(departure, destination, travelDate);

            model.addAttribute("trips", trips);
            model.addAttribute("departure", departure);
            model.addAttribute("destination", destination);
            model.addAttribute("date", date);

            return "passenger/trip-results";
        } catch (Exception e) {
            // Nếu date không đúng định dạng hoặc lỗi khác
            return "redirect:/?error";
        }
    }

@GetMapping("/confirm")
@Transactional // Quan trọng để đảm bảo tính toàn vẹn dữ liệu
public String confirmBooking(@RequestParam Long tripId, @RequestParam Long seatId, Model model) {
    // 1. Tìm ghế với cơ chế PESSIMISTIC_WRITE để tránh 2 người cùng nhấn 1 lúc
    Seat seat = seatRepository.findByIdWithLock(seatId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy ghế"));

    // 2. Kiểm tra nếu ghế đã bị ai đó giữ hoặc đã đặt
    if (seat.getStatus() != SeatStatus.AVAILABLE) {
        return "redirect:/booking/select-seat?tripId=" + tripId + "&error=occupied";
    }

    // 3. Tiến hành giữ chỗ tạm thời
    seat.setStatus(SeatStatus.PENDING);
    seat.setLockedAt(LocalDateTime.now());
    seatRepository.save(seat);

    model.addAttribute("trip", tripRepository.findById(tripId).orElse(null));
    model.addAttribute("seat", seat);
    return "passenger/confirm-booking";
}
    @GetMapping("/cancel-selection")
    @Transactional
    public String cancelSelection(@RequestParam Long tripId, @RequestParam Long seatId) {
        Seat seat = seatRepository.findById(seatId).orElse(null);
        if (seat != null && seat.getStatus() == SeatStatus.PENDING) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedAt(null);
            seatRepository.save(seat);
        }
        return "redirect:/booking/select-seat?tripId=" + tripId;
    }

}