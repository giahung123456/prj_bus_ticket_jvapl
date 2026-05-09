package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.entity.Trip;
import com.example.bus_ticket_prj_jvapl.repository.BusRepository;
import com.example.bus_ticket_prj_jvapl.repository.RouteRepository;
import com.example.bus_ticket_prj_jvapl.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/trips")
public class TripController {
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private BusRepository busRepository;

    @GetMapping
    public String listTrips(Model model) {
        model.addAttribute("trips", tripRepository.findAll());
        model.addAttribute("routes", routeRepository.findAll());
        model.addAttribute("buses", busRepository.findAll());
        model.addAttribute("activePage", "trips"); // Highlight sidebar
        return "admin/trips/index";
    }

    @PostMapping("/save")
    public String saveTrip(@ModelAttribute Trip trip) {
        // Logic: Khi lưu chuyến đi, bạn có thể cần tự động tạo ghế cho chuyến đó
        tripRepository.save(trip);
        return "redirect:/admin/trips";
    }
}
