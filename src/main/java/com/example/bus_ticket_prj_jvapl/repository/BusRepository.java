package com.example.bus_ticket_prj_jvapl.repository;

// BusRepository.java

import com.example.bus_ticket_prj_jvapl.model.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
}