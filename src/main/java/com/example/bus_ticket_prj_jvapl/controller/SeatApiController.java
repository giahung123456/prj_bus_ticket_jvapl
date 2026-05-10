package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.entity.Seat;
import com.example.bus_ticket_prj_jvapl.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // Dùng RestController để trả về dữ liệu JSON thay vì trang HTML
@RequestMapping("/api/seats")
public class SeatApiController {

    @Autowired
    private SeatRepository seatRepository;

    // Class DTO để đóng gói dữ liệu trả về cho nhẹ (chỉ gửi cái cần thiết)
    public static class SeatResponse {
        public Long id;
        public String status;
        public String seatNumber;

        public SeatResponse(Long id, String status, String seatNumber) {
            this.id = id;
            this.status = status;
            this.seatNumber = seatNumber;
        }
    }

    @GetMapping("/status/{tripId}")
    public ResponseEntity<List<SeatResponse>> getSeatsStatus(@PathVariable Long tripId) {
        try {
            // Lấy danh sách ghế từ DB
            List<Seat> seats = seatRepository.findByTripId(tripId);

            // Chuyển đổi từ Entity sang DTO để gửi về trình duyệt
            List<SeatResponse> response = seats.stream()
                    .map(s -> new SeatResponse(
                            s.getId(),
                            s.getStatus().name(), // Trả về "AVAILABLE", "PENDING", hoặc "BOOKED"
                            s.getSeatNumber()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Nếu có lỗi, trả về 500 để không làm treo trình duyệt
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}