package com.example.bus_ticket_prj_jvapl.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buses")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
@Data
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plateNumber; // Biển số xe
    private String busType;   // VD: 29 chỗ, 45 chỗ
    private Integer totalSeats;
    private String driverName; // Fix cứng theo SRS
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
}