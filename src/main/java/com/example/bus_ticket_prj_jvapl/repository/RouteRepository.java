package com.example.bus_ticket_prj_jvapl.repository;


import com.example.bus_ticket_prj_jvapl.model.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
}