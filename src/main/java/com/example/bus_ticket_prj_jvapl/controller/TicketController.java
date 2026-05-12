package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.dto.BookingRequestDTO;
import com.example.bus_ticket_prj_jvapl.model.entity.Ticket;
import com.example.bus_ticket_prj_jvapl.model.entity.User;
import com.example.bus_ticket_prj_jvapl.model.entity.UserProfile;
import com.example.bus_ticket_prj_jvapl.repository.SeatRepository;
import com.example.bus_ticket_prj_jvapl.repository.TripRepository;
import com.example.bus_ticket_prj_jvapl.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
//@RequestMapping("/book-ticket")
public class TicketController {

    @Autowired
    private BookingService bookingService;
    @Autowired private TripRepository tripRepository;
    @Autowired private SeatRepository seatRepository;
    @PostMapping("/book-ticket")
    public String processBooking(@ModelAttribute BookingRequestDTO dto,
                                 HttpSession session,
                                 Model model) {

        User user = (User) session.getAttribute("loggedInUser");
        UserProfile profile = user.getProfile();
        boolean hasError = false;

        // --- KIỂM TRA HỌ TÊN ---
        if (dto.getCustomerName() == null || dto.getCustomerName().trim().isEmpty()) {
            model.addAttribute("errorName", "Họ tên không được để trống!");
            hasError = true;
        }
        // Nếu đã nhập rồi thì mới kiểm tra xem có trùng tài khoản không
        else if (!dto.getCustomerName().trim().equalsIgnoreCase(profile.getFullName())) {
            model.addAttribute("errorName", "Họ tên phải trùng khớp với thông tin tài khoản!");
            hasError = true;
        }

        // --- KIỂM TRA SỐ ĐIỆN THOẠI ---
        if (dto.getCustomerPhone() == null || dto.getCustomerPhone().trim().isEmpty()) {
            model.addAttribute("errorPhone", "Số điện thoại không được để trống!");
            hasError = true;
        }
        // Nếu đã nhập rồi thì mới kiểm tra xem có trùng tài khoản không
        else if (!dto.getCustomerPhone().trim().equals(profile.getPhoneNumber())) {
            model.addAttribute("errorPhone", "Số điện thoại phải trùng khớp với thông tin tài khoản!");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("profile", profile);
            model.addAttribute("trip", tripRepository.findById(dto.getTripId()).orElse(null));
            model.addAttribute("seat", seatRepository.findById(dto.getSeatId()).orElse(null));

            model.addAttribute("customerName", dto.getCustomerName());
            model.addAttribute("customerPhone", dto.getCustomerPhone());

            return "passenger/confirm-booking";
        }

        try {
            Ticket ticket = bookingService.createBooking(dto);
            return "redirect:/passenger/success/" + ticket.getTicketCode();
        } catch (Exception e) {
            return "redirect:/booking/select-seat?tripId=" + dto.getTripId() + "&error=" + e.getMessage();
        }
    }
    // Trong BookingController.java hoặc TicketController.java

    @GetMapping("/passenger/success/{ticketCode}")
    public String bookingSuccess(@PathVariable String ticketCode, Model model) {
        // Truyền mã vé sang giao diện để hiển thị cho khách hàng
        model.addAttribute("ticketCode", ticketCode);

        // Trả về file HTML (ví dụ: src/main/resources/templates/passenger/success.html)
        return "passenger/success";
    }
}