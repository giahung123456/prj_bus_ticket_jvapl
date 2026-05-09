package com.example.bus_ticket_prj_jvapl.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trips")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    private LocalDateTime departureTime;
    private Double price;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Seat> seats;
}