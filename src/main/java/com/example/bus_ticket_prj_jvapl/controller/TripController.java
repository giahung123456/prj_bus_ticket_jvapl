package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.entity.Bus;
import com.example.bus_ticket_prj_jvapl.model.entity.Seat;
import com.example.bus_ticket_prj_jvapl.model.entity.Trip;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.SeatStatus;
import com.example.bus_ticket_prj_jvapl.repository.BusRepository;
import com.example.bus_ticket_prj_jvapl.repository.RouteRepository;
import com.example.bus_ticket_prj_jvapl.repository.SeatRepository;
import com.example.bus_ticket_prj_jvapl.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired private SeatRepository seatRepository; // Thêm Repository này

    @GetMapping
    public String listTrips(Model model) {
        model.addAttribute("trips", tripRepository.findAll());
        model.addAttribute("routes", routeRepository.findAll());
        model.addAttribute("buses", busRepository.findAll());
        model.addAttribute("activePage", "trips"); // Highlight sidebar
        return "admin/trips/index";
    }

//    @PostMapping("/save")
//    public String saveTrip(@ModelAttribute Trip trip) {
//        // Logic: Khi lưu chuyến đi, bạn có thể cần tự động tạo ghế cho chuyến đó
//        tripRepository.save(trip);
//        return "redirect:/admin/trips";
//    }
//    @PostMapping("/save")
//    @Transactional // Thêm cái này để đảm bảo lưu Trip và Seat cùng thành công hoặc cùng thất bại
//    public String saveTrip(@ModelAttribute Trip trip) {
//        // 1. Lưu chuyến đi vào DB
//        Trip savedTrip = tripRepository.save(trip);
//
//        // 2. Lấy thông tin đầy đủ của xe (bao gồm số lượng ghế)
//        // Khi Admin gửi form, trip.getBus() thường chỉ có ID. Ta cần tìm lại trong DB.
//        Bus bus = busRepository.findById(savedTrip.getBus().getId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe"));
//
//        // 3. Tự động tạo ghế dựa trên capacity (số chỗ) của xe
//        // Giả sử trong class Bus, trường lưu số ghế là 'capacity' hoặc 'totalSeats'
//        int totalSeats = bus.getTotalSeats();
//
//        for (int i = 1; i <= totalSeats; i++) {
//            Seat seat = Seat.builder()
//                    .seatNumber("G" + i) // Đặt tên ghế: G1, G2, G3...
//                    .status(SeatStatus.AVAILABLE) // Mặc định là ghế trống
//                    .trip(savedTrip) // Gán vào chuyến đi vừa tạo
//                    .build();
//
//            seatRepository.save(seat);
//        }
//
//        return "redirect:/admin/trips";
//    }
@PostMapping("/save")
@Transactional
public String saveTrip(@ModelAttribute Trip trip) {
    Trip savedTrip = tripRepository.save(trip);
    Bus bus = busRepository.findById(savedTrip.getBus().getId()).orElseThrow();
    int totalSeats = bus.getTotalSeats();

    if ("Giường nằm".equals(bus.getBusType())) {
        int seatsPerFloor = totalSeats / 2;
        // Tầng dưới (A)
        for (int i = 1; i <= seatsPerFloor; i++) {
            createSeat("A" + i, savedTrip);
        }
        // Tầng trên (B)
        for (int i = 1; i <= (totalSeats - seatsPerFloor); i++) {
            createSeat("B" + i, savedTrip);
        }
    } else {
        // Xe ghế ngồi hoặc Limousine thì dùng chữ 'G' hoặc 'A' tùy bạn
        // Nhưng nếu HTML của bạn dùng th:unless cho xe thường,
        // bạn nên để là "A" hoặc bỏ điều kiện check startsWith ở HTML.
        for (int i = 1; i <= totalSeats; i++) {
            createSeat("A" + i, savedTrip);
        }
    }
    return "redirect:/admin/trips";
}

    // Hàm phụ để đỡ viết lặp code
    private void createSeat(String number, Trip trip) {
        Seat seat = Seat.builder()
                .seatNumber(number)
                .status(SeatStatus.AVAILABLE)
                .trip(trip)
                .build();
        seatRepository.save(seat);
    }
}
