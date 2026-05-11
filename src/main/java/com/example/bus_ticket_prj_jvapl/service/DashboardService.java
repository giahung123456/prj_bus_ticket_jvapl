package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {
    @Autowired
    private TicketRepository ticketRepository;

    public Map<String, Object> getAdminDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Lấy doanh thu tuyến đường
        stats.put("routeRevenue", ticketRepository.getRevenueStatsByRoute());

        // Lấy Top 5 (Dùng PageRequest để giới hạn 5 bản ghi)
        stats.put("topTrips", ticketRepository.findTop5ActiveTrips(PageRequest.of(0, 5)));

        return stats;
    }
}