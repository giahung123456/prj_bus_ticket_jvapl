package com.example.bus_ticket_prj_jvapl.model.entity;

import com.example.bus_ticket_prj_jvapl.model.entity.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticketCode;

    // Thông tin khách hàng (có thể lấy từ User hoặc nhập trực tiếp)
    private String customerName;
    private String customerPhone;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @OneToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();
}