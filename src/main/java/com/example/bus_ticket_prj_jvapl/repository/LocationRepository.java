// LocationRepository.java (Cần thiết để lưu Tỉnh thành trước khi gán vào Tuyến)
package com.example.bus_ticket_prj_jvapl.repository;
import com.example.bus_ticket_prj_jvapl.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByName(String name);
}