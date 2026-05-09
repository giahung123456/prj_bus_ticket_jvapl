package com.example.bus_ticket_prj_jvapl.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/default")
    public String redirectAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/index";
        } else if (request.isUserInRole("ROLE_STAFF")) {
            return "redirect:/staff/index";
        }
        return "redirect:/passenger/home";
    }

    @GetMapping("/admin/index") public String admin() { return "admin/index"; }
    @GetMapping("/staff/index") public String staff() { return "staff/index"; }
    @GetMapping("/passenger/home") public String passenger() { return "passenger/home"; }
}