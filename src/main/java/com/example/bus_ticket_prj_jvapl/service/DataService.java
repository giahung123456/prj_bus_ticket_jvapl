package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.model.entity.*;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.SeatStatus;
import com.example.bus_ticket_prj_jvapl.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService {
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @PostConstruct
    public void initBusData() {

        // 1. Xóa các bảng phụ thuộc trước (Child tables)
        ticketRepository.deleteAll(); // Xóa vé trước
        seatRepository.deleteAll();   // Xóa ghế (vì ghế nối với Trip)

        // 2. Xóa các bảng trung gian
        tripRepository.deleteAll();   // Xóa chuyến đi
        busRepository.deleteAll();    // Xóa xe

        // 3. Xóa các bảng danh mục (Master tables)
        routeRepository.deleteAll();  // Xóa tuyến đường
        locationRepository.deleteAll();
        if (locationRepository.count() == 0) {
            // 1. Seed Tỉnh thành (Location)
            Location hn = Location.builder().name("Hà Nội").build();
            Location hp = Location.builder().name("Hải Phòng").build();
            Location sg = Location.builder().name("Sài Gòn").build();
            Location dl = Location.builder().name("Đà Lạt").build();
            locationRepository.saveAll(List.of(hn, hp, sg, dl));

            // 2. Seed Tuyến đường (Gán object Location vào, không gán String)
            Route r1 = Route.builder()
                    .departure(hn)
                    .destination(hp)
                    .distance(120)
                    .build();
            Route r2 = Route.builder()
                    .departure(sg)
                    .destination(dl)
                    .distance(300)
                    .build();

            routeRepository.saveAll(List.of(r1, r2));

            // 3. Seed Xe mẫu
            Bus b1 = Bus.builder()
                    .plateNumber("29B-12345")
                    .busType("Giường nằm")
                    .totalSeats(34)
                    .route(r1)
                    .build();
            busRepository.save(b1);
            Bus b2 = Bus.builder()
                    .plateNumber("29B-76378")
                    .busType("Ghế ngồi")
                    .totalSeats(18)
                    .route(r2)
                    .build();
            busRepository.save(b2);
            Bus b3 = Bus.builder()
                    .plateNumber("28B-43892")
                    .busType("Limousine cao cấp")
                    .totalSeats(22)
                    .route(r1)
                    .build();
            busRepository.save(b3);
// 4. SEED CHUYẾN ĐI (TRIP) - Đây là thứ mà nút TÌM KIẾM cần thấy
            Trip t1 = Trip.builder()
                    .route(r1) // Tuyến Hà Nội - Hải Phòng
                    .bus(b1)
                    .price(150000.0)
                    // Lưu ý: Đặt ngày là 2026-05-10 để bạn dễ chọn trên lịch khi test
                    .departureTime(java.time.LocalDateTime.of(2026, 5, 10, 8, 0))
                    .build();

            tripRepository.save(t1);
// Chuyến 2: Xe ghế ngồi
            Trip t2 = Trip.builder().route(r2).bus(b2).price(250000.0)
                    .departureTime(java.time.LocalDateTime.of(2026, 5, 10, 14, 0)).build();
            tripRepository.save(t2);

            // Chuyến 3: XE LIMOUSINE CAO CẤP (Thêm theo yêu cầu của bạn)
            Trip t3 = Trip.builder().route(r1).bus(b3).price(350000.0)
                    .departureTime(java.time.LocalDateTime.of(2026, 5, 10, 20, 0)).build();
            tripRepository.save(t3);

            List<Trip> allTrips = List.of(t1, t2, t3);
            for (Trip trip : allTrips) {
                int totalSeats = trip.getBus().getTotalSeats();
                String type = trip.getBus().getBusType();

                if ("Giường nằm".equalsIgnoreCase(type)) {
                    int seatsPerDeck = totalSeats / 2;
                    for (int i = 1; i <= seatsPerDeck; i++) {
                        seatRepository.save(Seat.builder().seatNumber("A" + i).status(SeatStatus.AVAILABLE).trip(trip).version(0L).build());
                        seatRepository.save(Seat.builder().seatNumber("B" + i).status(SeatStatus.AVAILABLE).trip(trip).version(0L).build());
                    }
                } else {
                    // Áp dụng cho cả "Ghế ngồi" và "Limousine cao cấp"
                    for (int i = 1; i <= totalSeats; i++) {
                        String prefix = type.contains("Limousine") ? "L" : "S";
                        seatRepository.save(Seat.builder().seatNumber(prefix + i).status(SeatStatus.AVAILABLE).trip(trip).version(0L).build());
                    }
                }
            }
                System.out.println(">>> DataSeed: Đã khởi tạo Tỉnh thành, Tuyến đường và Xe mẫu.");
            }
        }

    }
