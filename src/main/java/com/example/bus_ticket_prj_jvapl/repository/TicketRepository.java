package com.example.bus_ticket_prj_jvapl.repository;

import com.example.bus_ticket_prj_jvapl.model.dto.RevenueByRouteDTO;
import com.example.bus_ticket_prj_jvapl.model.dto.TicketDetailDTO;
import com.example.bus_ticket_prj_jvapl.model.dto.TopTripDTO;
import com.example.bus_ticket_prj_jvapl.model.entity.Ticket;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Tìm danh sách vé của chuyến để check xem ghế nào đã được bán
    List<Ticket> findByTripId(Long tripId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Optional<Ticket> findByIdWithLock(@Param("id") Long id);
    @Query("SELECT new com.example.bus_ticket_prj_jvapl.model.dto.TicketDetailDTO(" +
            "t.ticketCode, t.customerName, t.customerPhone, s.seatNumber, " +
            "b.plateNumber, r.departure.name, r.destination.name, " +
            "tr.departureTime, t.status, t.totalPrice) " +
            "FROM Ticket t " +
            "JOIN t.seat s " +
            "JOIN t.trip tr " +
            "JOIN tr.route r " +
            "JOIN tr.bus b " +
            "WHERE t.ticketCode = :code AND t.customerPhone = :phone")
    Optional<TicketDetailDTO> findTicketDetail(@Param("code") String code,
                                               @Param("phone") String phone);
    List<Ticket> findByStatus(TicketStatus status);
    // Dùng để kiểm tra nhanh trước khi lưu (Conflict Check)
    boolean existsByTripIdAndSeatId(Long tripId, Long seatId);

    // Trong TicketRepository.java
    @Query("SELECT t FROM Ticket t JOIN FETCH t.trip WHERE t.ticketCode = :code AND t.customerPhone = :phone")
    Optional<Ticket> findByTicketCodeAndPhone(@Param("code") String code, @Param("phone") String phone);

    // 1. Thống kê theo tuyến (Dùng Class DTO)
    @Query("SELECT new com.example.bus_ticket_prj_jvapl.model.dto.RevenueByRouteDTO(" +
            "t.trip.route.departure.name || ' - ' || t.trip.route.destination.name, " +
            "COUNT(t), SUM(t.totalPrice)) " +
            "FROM Ticket t " +
            "WHERE t.status = com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus.PAID " +
            "GROUP BY t.trip.route.id, t.trip.route.departure.name, t.trip.route.destination.name")
    List<RevenueByRouteDTO> getRevenueStatsByRoute();

    @Query("SELECT new com.example.bus_ticket_prj_jvapl.model.dto.TopTripDTO(" +
            "t.trip.id, " +
            "t.trip.route.departure.name || ' - ' || t.trip.route.destination.name, " +
            "t.trip.departureTime, " +
            "COUNT(t), " +
            "t.trip.bus.plateNumber, " + // Lấy biển số
            "t.trip.bus.busType) " +     // Lấy loại xe (Giường nằm/Ghế ngồi)
            "FROM Ticket t " +
            "GROUP BY t.trip.id, t.trip.route.departure.name, t.trip.route.destination.name, " +
            "t.trip.departureTime, t.trip.bus.plateNumber, t.trip.bus.busType " +
            "ORDER BY COUNT(t) DESC")
    List<TopTripDTO> findTop5ActiveTrips(Pageable pageable);

    // 3. Doanh thu tháng hiện tại
    @Query("SELECT SUM(t.totalPrice) FROM Ticket t " +
            "WHERE t.status = com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus.PAID " +
            "AND MONTH(t.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(t.createdAt) = YEAR(CURRENT_DATE)")
    Double getTotalRevenueThisMonth();


    // Xóa dòng cũ và thay bằng dòng này trong TicketRepository
    @Query("SELECT t FROM Ticket t WHERE t.customerPhone = :phone ORDER BY t.createdAt DESC")
    List<Ticket> findByCustomerPhone(@Param("phone") String phone);
}