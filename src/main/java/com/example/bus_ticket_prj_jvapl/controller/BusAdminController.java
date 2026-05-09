//package com.example.bus_ticket_prj_jvapl.controller;
//
//import com.example.bus_ticket_prj_jvapl.model.entity.Bus;
//import com.example.bus_ticket_prj_jvapl.repository.BusRepository;
//import com.example.bus_ticket_prj_jvapl.repository.RouteRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@RequestMapping("/admin/buses")
//public class BusAdminController {
//    @Autowired
//    private BusRepository busRepository;
//    @Autowired private RouteRepository routeRepository;
//
//    @GetMapping
//    public String listBuses(Model model) {
//        model.addAttribute("buses", busRepository.findAll());
//        return "admin/bus/list";
//    }
//
//    @GetMapping("/add")
//    public String showAddForm(Model model) {
//        model.addAttribute("bus", new Bus());
//        model.addAttribute("routes", routeRepository.findAll());
//        return "admin/bus/form";
//    }
//
//    @PostMapping("/save")
//    public String saveBus(@ModelAttribute Bus bus) {
//        busRepository.save(bus);
//        return "redirect:/admin/buses";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteBus(@PathVariable Long id) {
//        busRepository.deleteById(id);
//        return "redirect:/admin/buses";
//    }
//}
package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.entity.Bus;
import com.example.bus_ticket_prj_jvapl.repository.BusRepository;
import com.example.bus_ticket_prj_jvapl.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/buses")
public class BusAdminController {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RouteRepository routeRepository;

    // 1. Hiển thị danh sách xe
    @GetMapping
    public String listBuses(Model model) {
        model.addAttribute("buses", busRepository.findAll());
        return "admin/bus/list";
    }

    // 2. Hiển thị form thêm mới
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("bus", new Bus());
        model.addAttribute("routes", routeRepository.findAll());
        return "admin/bus/form";
    }

    // 3. HIỂN THỊ FORM SỬA XE (Bị thiếu hàm này dẫn đến lỗi 404)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe có ID: " + id));

        model.addAttribute("bus", bus);
        model.addAttribute("routes", routeRepository.findAll()); // Lấy danh sách tuyến để chọn lại nếu cần
        return "admin/bus/form"; // Dùng chung form với chức năng Add
    }

    // 4. Lưu thông tin (Dùng chung cho cả Thêm và Sửa)
    @PostMapping("/save")
    public String saveBus(@ModelAttribute Bus bus) {
        busRepository.save(bus);
        return "redirect:/admin/buses";
    }

    // 5. Xóa xe
    @GetMapping("/delete/{id}")
    public String deleteBus(@PathVariable Long id) {
        busRepository.deleteById(id);
        return "redirect:/admin/buses";
    }
}