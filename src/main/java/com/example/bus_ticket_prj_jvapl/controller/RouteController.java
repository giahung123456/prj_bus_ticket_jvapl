package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.entity.Route;
import com.example.bus_ticket_prj_jvapl.repository.LocationRepository;
import com.example.bus_ticket_prj_jvapl.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/routes")
public class RouteController {
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private LocationRepository locationRepository;

    @GetMapping
    public String listRoutes(Model model) {
        model.addAttribute("routes", routeRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("activePage", "routes"); // Để highlight sidebar
        return "admin/routes/index";
    }

    @PostMapping("/save")
    public String saveRoute(@ModelAttribute Route route) {
        routeRepository.save(route);
        return "redirect:/admin/routes";
    }
}