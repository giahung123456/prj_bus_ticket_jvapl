package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.dto.TicketDetailDTO;
import com.example.bus_ticket_prj_jvapl.model.entity.User;
import com.example.bus_ticket_prj_jvapl.service.TicketService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tickets")
public class TicketLookupController {

    @Autowired
    private TicketService ticketService;

    // Trang nhập mã vé + SĐT
    @GetMapping("/lookup")
    public String showLookupPage() {
        return "passenger/ticket-lookup";
    }

    // Xử lý tra cứu
    @GetMapping("/detail")
    public String viewTicketDetail(@RequestParam String code,
                                   @RequestParam String phone,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes ra) {

        User user = (User) session.getAttribute("loggedInUser");
        boolean hasError = false;

        // 1. Kiểm tra trống mã vé
        if (code == null || code.trim().isEmpty()) {
            ra.addFlashAttribute("errorCode", "Mã vé không được để trống!");
            hasError = true;
        }

        // 2. Kiểm tra trống số điện thoại
        if (phone == null || phone.trim().isEmpty()) {
            ra.addFlashAttribute("errorPhone", "Số điện thoại không được để trống!");
            hasError = true;
        }
        // 3. Nếu đã nhập SĐT, kiểm tra xem có đúng là SĐT của tài khoản đang đăng nhập không
        else if (user != null && !phone.trim().equals(user.getProfile().getPhoneNumber())) {
            ra.addFlashAttribute("errorPhone", "Số điện thoại phải trùng khớp với số đăng ký tài khoản!");
            hasError = true;
        }

        if (hasError) {
            // Giữ lại dữ liệu đã nhập để người dùng không phải gõ lại
            ra.addFlashAttribute("inputCode", code);
            ra.addFlashAttribute("inputPhone", phone);
            return "redirect:/tickets/lookup";
        }

        try {
            TicketDetailDTO detail = ticketService.getTicketDetail(code, phone);
            model.addAttribute("ticket", detail);
            return "passenger/ticket-detail";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/tickets/lookup";
        }
    }
    // Trong TicketLookupController.java
    @PostMapping("/cancel")
    public String cancelTicket(@RequestParam String code,
                               @RequestParam String phone,
                               RedirectAttributes ra) {
        try {
            ticketService.cancelTicketProactive(code, phone);
            ra.addFlashAttribute("success", "Hủy vé thành công. Ghế của bạn đã được giải phóng.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tickets/lookup";
    }
}