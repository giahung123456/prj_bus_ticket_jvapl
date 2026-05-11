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

//    // 1. Thống kê doanh thu theo Tuyến đường (Dùng JPQL)
//    @Query("SELECT t.trip.route.departure.name || ' - ' || t.trip.route.destination.name as routeName, " +
//            "COUNT(t) as ticketCount, SUM(t.totalPrice) as totalRevenue " +
//            "FROM Ticket t " +
//            "WHERE t.status = 'CONFIRMED' " +
//            "GROUP BY t.trip.route.id, t.trip.route.departure.name, t.trip.route.destination.name")
//    List<RevenueByRouteDTO> getRevenueStatsByRoute();

// 1. Thống kê theo tuyến
@Query("SELECT t.trip.route.departure.name || ' - ' || t.trip.route.destination.name as routeName, " +
        "COUNT(t) as ticketCount, SUM(t.totalPrice) as totalRevenue " +
        "FROM Ticket t " +
        "WHERE t.status = com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus.PAID " + // Sửa ở đây
        "GROUP BY t.trip.route.id, t.trip.route.departure.name, t.trip.route.destination.name")
List<RevenueByRouteDTO> getRevenueStatsByRoute();

//    // 2. Thống kê doanh thu theo Tháng (Dùng Native Query vì hàm xử lý ngày tháng tùy thuộc vào loại DB)
//    // Ví dụ cho MySQL:
//    @Query(value = "SELECT MONTHNAME(t.created_at) as month, SUM(t.total_price) as revenue " +
//            "FROM tickets t WHERE t.status = 'CONFIRMED' AND YEAR(t.created_at) = YEAR(CURDATE()) " +
//            "GROUP BY MONTH(t.created_at) " +
//            "ORDER BY MONTH(t.created_at)", nativeQuery = true)
//    List<Object[]> getMonthlyRevenue();
// 3. Native Query (Thống kê theo tháng)
@Query(value = "SELECT MONTHNAME(t.created_at) as month, SUM(t.total_price) as revenue " +
        "FROM tickets t WHERE t.status = 'PAID' " + // Sửa ở đây (String cho Native Query)
        "AND YEAR(t.created_at) = YEAR(CURDATE()) " +
        "GROUP BY MONTH(t.created_at) " +
        "ORDER BY MONTH(t.created_at)", nativeQuery = true)
List<Object[]> getMonthlyRevenue();
    // 3. Top 5 chuyến xe có lượt đặt cao nhất (Dùng JPQL)
    @Query("SELECT t.trip.id as tripId, t.trip.route.departure.name || ' - ' || t.trip.route.destination.name as routeName, " +
            "t.trip.departureTime as departureTime, COUNT(t) as bookingCount " +
            "FROM Ticket t " +
            "GROUP BY t.trip.id, t.trip.route.departure.name, t.trip.route.destination.name, t.trip.departureTime " +
            "ORDER BY COUNT(t) DESC")
    List<TopTripDTO> findTop5ActiveTrips(Pageable pageable);

//    @Query("SELECT SUM(t.totalPrice) FROM Ticket t " +
//            "WHERE t.status = com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus.CONFIRMED " +
//            "AND MONTH(t.createdAt) = MONTH(CURRENT_DATE) " +
//            "AND YEAR(t.createdAt) = YEAR(CURRENT_DATE)")
//    Double getTotalRevenueThisMonth();
// 2. Doanh thu tháng hiện tại
@Query("SELECT SUM(t.totalPrice) FROM Ticket t " +
        "WHERE t.status = com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus.PAID " + // Sửa ở đây
        "AND MONTH(t.createdAt) = MONTH(CURRENT_DATE) " +
        "AND YEAR(t.createdAt) = YEAR(CURRENT_DATE)")
Double getTotalRevenueThisMonth();
}