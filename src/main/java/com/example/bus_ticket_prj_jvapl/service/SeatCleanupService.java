package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.model.entity.Seat;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.SeatStatus;
import com.example.bus_ticket_prj_jvapl.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeatCleanupService {

    @Autowired
    private SeatRepository seatRepository;

    // Chạy mỗi 60 giây (60000ms)
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void releaseExpiredSeats() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        // Tìm các ghế PENDING có lockedAt cũ hơn 10 phút
        List<Seat> expiredSeats = seatRepository.findAll().stream()
                .filter(s -> s.getStatus() == SeatStatus.PENDING
                        && s.getLockedAt() != null
                        && s.getLockedAt().isBefore(tenMinutesAgo))
                .toList();

        for (Seat seat : expiredSeats) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockedAt(null);
        }
        seatRepository.saveAll(expiredSeats);
    }
}