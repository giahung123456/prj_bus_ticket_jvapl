package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.model.entity.Seat;
import com.example.bus_ticket_prj_jvapl.model.entity.Trip;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.SeatStatus;
import com.example.bus_ticket_prj_jvapl.repository.SeatRepository;
import com.example.bus_ticket_prj_jvapl.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TripService {

    @Autowired private TripRepository tripRepository;
    @Autowired private SeatRepository seatRepository;

    @Transactional
    public void saveTrip(Trip trip) {
        // 1. Lưu chuyến đi (Lúc này Trip đã có thông tin Bus do Admin chọn từ Form)
        Trip savedTrip = tripRepository.save(trip);

        // 2. Lấy số lượng ghế từ xe của chuyến đi này
        // Giả sử trong entity Trip bạn có: private Bus bus;
        // Và trong entity Bus có: private int totalSeats;
        int numberOfSeats = savedTrip.getBus().getTotalSeats();

        // 3. Vòng lặp tạo đúng số lượng ghế của xe đó
        for (int i = 1; i <= numberOfSeats; i++) {
            Seat seat = Seat.builder()
                    .seatNumber("G" + i) // Đặt tên ghế theo số thứ tự (ví dụ: G1, G2...)
                    .status(SeatStatus.AVAILABLE)
                    .trip(savedTrip)
                    .build();

            seatRepository.save(seat);
        }
    }
}