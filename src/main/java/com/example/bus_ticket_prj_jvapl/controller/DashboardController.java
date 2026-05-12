package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.service.BusService;
import com.example.bus_ticket_prj_jvapl.service.DashboardService;
import com.example.bus_ticket_prj_jvapl.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {
    @Autowired private DashboardService dashboardService;
    @Autowired
    TicketService ticketService;
    @Autowired private BusService busService;
    @GetMapping("/default")
    public String redirectAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/index";
        } else if (request.isUserInRole("ROLE_STAFF")) {
            return "redirect:/staff/index";
        }
        return "redirect:/passenger/home";
    }
    // ĐÂY LÀ NƠI VIẾT LOGIC THỐNG KÊ
    @GetMapping("/admin/index")
    public String adminDashboard(Model model) {
        // Gọi service lấy data thống kê
        Map<String, Object> stats = dashboardService.getAdminDashboardStats();

        model.addAttribute("routeRevenue", stats.get("routeRevenue"));
        model.addAttribute("topTrips", stats.get("topTrips"));
        model.addAttribute("activePage", "dashboard");

        // Thêm các con số tổng quát
        model.addAttribute("totalBuses", busService.countAll());
        model.addAttribute("monthlyRevenue", ticketService.calculateMonthlyRevenue());

        return "admin/index"; // Trả về file templates/admin/index.html
    }

    @GetMapping("/staff/index") public String staff() { return "staff/index"; }
    @GetMapping("/passenger/home") public String passenger() { return "passenger/home"; }

}