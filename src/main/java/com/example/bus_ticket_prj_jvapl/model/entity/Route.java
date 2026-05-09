package com.example.bus_ticket_prj_jvapl.model.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "departure_id")
    private Location departure;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Location destination;

    private Integer distance;
}