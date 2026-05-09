package com.example.bus_ticket_prj_jvapl.repository;



import com.example.bus_ticket_prj_jvapl.model.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Truy vấn 3 tầng: Trip -> Route -> Location -> name
    @Query("SELECT t FROM Trip t WHERE t.route.departure.name = :dep " +
            "AND t.route.destination.name = :dest " +
            "AND CAST(t.departureTime AS date) = :date")
    List<Trip> findAvailableTrips(@Param("dep") String departure,
                                  @Param("dest") String destination,
                                  @Param("date") LocalDate date);

//    // Lấy danh sách điểm đi duy nhất từ bảng Location thông qua Route
//    @Query("SELECT DISTINCT t.route.departure.name FROM Trip t")
//    List<String> findAllDeparturePoints();
//
//    // Lấy danh sách điểm đến duy nhất từ bảng Location thông qua Route
//    @Query("SELECT DISTINCT t.route.destination.name FROM Trip t")
//    List<String> findAllDestinationPoints();
// Lấy từ Route (Điểm đi/đến nào có trong tuyến đường thì hiện lên)
@Query("SELECT DISTINCT r.departure.name FROM Route r")
List<String> findAllDeparturePoints();

    @Query("SELECT DISTINCT r.destination.name FROM Route r")
    List<String> findAllDestinationPoints();
    List<Trip> findByDepartureTimeAfterOrderByDepartureTimeAsc(LocalDateTime now);
}