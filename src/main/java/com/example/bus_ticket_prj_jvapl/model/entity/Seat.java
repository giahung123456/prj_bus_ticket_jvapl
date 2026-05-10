package com.example.bus_ticket_prj_jvapl.model.entity;

import com.example.bus_ticket_prj_jvapl.model.entity.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats")

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Version
    private Long version;
    private LocalDateTime lockedAt;

}