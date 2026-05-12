package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.entity.Ticket;
import com.example.bus_ticket_prj_jvapl.model.entity.User;
import com.example.bus_ticket_prj_jvapl.repository.TicketRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/history")
public class UserController {

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public String showHistory(HttpSession session, Model model) {
        // 1. Lấy user từ session
        User user = (User) session.getAttribute("loggedInUser");

        // 2. Kiểm tra đăng nhập
        if (user == null) {
            return "redirect:/login";
        }

        // 3. Lấy lịch sử vé theo số điện thoại
        // Lưu ý: Đảm bảo đường dẫn lấy Phone đúng với cấu trúc User của bạn
        String phone = user.getProfile().getPhoneNumber();
        List<Ticket> tickets = ticketRepository.findByCustomerPhone(phone);

        model.addAttribute("tickets", tickets);
        return "passenger/history";
    }
}