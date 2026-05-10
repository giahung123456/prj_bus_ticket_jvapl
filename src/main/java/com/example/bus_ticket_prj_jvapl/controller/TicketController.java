package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.dto.BookingRequestDTO;
import com.example.bus_ticket_prj_jvapl.model.entity.Ticket;
import com.example.bus_ticket_prj_jvapl.service.BookingService;
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

    @PostMapping("/book-ticket")
    public String processBooking(@ModelAttribute BookingRequestDTO dto, RedirectAttributes ra) {
        try {
            // ĐẶT LOG TẠI ĐÂY
            System.err.println("--- DEBUG BOOKING ---");
            System.err.println("Trip ID gửi lên: " + dto.getTripId());
            System.err.println("Seat ID gửi lên: " + dto.getSeatId()); // Nếu cái này null là lỗi do HTML
            System.err.println("Tên khách: " + dto.getCustomerName());
            // Truyền thẳng object dto vào service
            Ticket ticket = bookingService.createBooking(dto);

            ra.addFlashAttribute("successMsg", "Đặt chỗ thành công! Mã vé: " + ticket.getTicketCode());
            return "redirect:/passenger/success/" + ticket.getTicketCode();
        } catch (Exception e) {
            // LOG LỖI CHI TIẾT
            System.err.println("LỖI KHI ĐẶT VÉ: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", e.getMessage());
            // Quay lại trang chọn ghế của chuyến đi đó
//            return "redirect:/passenger/select-seat?tripId=" + dto.getTripId();
            return "redirect:/booking/select-seat?tripId=" + dto.getTripId();
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