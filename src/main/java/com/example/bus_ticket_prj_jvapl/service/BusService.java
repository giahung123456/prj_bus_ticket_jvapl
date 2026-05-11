package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    // Thêm hàm này vào
    public long countAll() {
        return busRepository.count(); // count() là hàm có sẵn của JpaRepository
    }
}