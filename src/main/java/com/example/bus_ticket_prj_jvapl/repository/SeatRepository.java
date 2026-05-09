package com.example.bus_ticket_prj_jvapl.repository;

import com.example.bus_ticket_prj_jvapl.model.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findByIdWithLock(@Param("id") Long id);

    // Spring Boot sẽ tự hiểu là tìm Seat theo Id của Trip gắn trực tiếp trong bảng Seats
    // Không cần viết @Query phức tạp nữa


    List<Seat> findByTripId(Long tripId);

    // Tìm ghế theo số ghế (A1, B2...) và TripId
    Optional<Seat> findBySeatNumberAndTripId(String seatNumber, Long tripId);
}