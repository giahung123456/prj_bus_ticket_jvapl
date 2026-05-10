package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.dto.TicketDetailDTO;
import com.example.bus_ticket_prj_jvapl.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
                                   Model model,
                                   RedirectAttributes ra) {
        try {
            TicketDetailDTO detail = ticketService.getTicketDetail(code, phone);
            model.addAttribute("ticket", detail);
            return "passenger/ticket-detail";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/tickets/lookup";
        }
    }
}