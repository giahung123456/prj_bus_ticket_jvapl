package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.service.BusService;
import com.example.bus_ticket_prj_jvapl.service.DashboardService;
import com.example.bus_ticket_prj_jvapl.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
// Thêm cái này để gom nhóm các link admin
public class AdminController {
    @Autowired private DashboardService dashboardService;
    @Autowired
    BusService busService;

    @Autowired
    TicketService ticketService;
    @GetMapping({"/index"})
    public String adminDashboard(Model model) {
        // Giả sử bạn đã viết DashboardService như hướng dẫn trước
        Map<String, Object> stats = dashboardService.getAdminDashboardStats();

        model.addAttribute("routeRevenue", stats.get("routeRevenue"));
        model.addAttribute("topTrips", stats.get("topTrips"));
        model.addAttribute("activePage", "dashboard");

        // Các con số tổng quát (Bạn có thể viết thêm hàm lấy tổng trong Service)
        model.addAttribute("totalBuses", busService.countAll());
        model.addAttribute("monthlyRevenue", ticketService.calculateMonthlyRevenue());

        return "admin/index";
    }
}
