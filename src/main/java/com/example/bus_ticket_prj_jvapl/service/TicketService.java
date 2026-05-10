package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.model.dto.TicketDetailDTO;
import com.example.bus_ticket_prj_jvapl.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    public TicketDetailDTO getTicketDetail(String code, String phone) {
        return ticketRepository.findTicketDetail(code, phone)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin vé phù hợp!"));
    }
}