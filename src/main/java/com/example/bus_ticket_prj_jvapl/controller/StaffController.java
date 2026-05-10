package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired private PaymentService paymentService;

    // Đường dẫn chính hiển thị danh sách vé chờ
//    @GetMapping("/tickets")
    @GetMapping({"", "/", "/tickets"})
    public String listPending(Model model) {
        System.err.println(">>> ĐANG VÀO TRANG NHÂN VIÊN"); // Dòng này cực kỳ quan trọng
        model.addAttribute("tickets", paymentService.getPendingTickets());
        return "staff/index"; // Trả về src/main/resources/templates/staff/index.html
    }

    // Xử lý nút "Xác nhận"
    @PostMapping("/confirm-payment/{id}")
    public String confirm(@PathVariable Long id, RedirectAttributes ra) {
        try {
            paymentService.confirmPayment(id);
            ra.addFlashAttribute("success", "Xác nhận thanh toán thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/tickets";
    }

    // Xử lý nút "Hủy"
    @PostMapping("/cancel-ticket/{id}")
    public String cancel(@PathVariable Long id, RedirectAttributes ra) {
        try {
            paymentService.cancelTicket(id);
            ra.addFlashAttribute("success", "Đã hủy vé và giải phóng ghế!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/tickets";
    }
}