package com.example.bus_ticket_prj_jvapl.repository;

import com.example.bus_ticket_prj_jvapl.model.entity.User;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    // Sửa CharSequence thành List<User>
    List<User> findByRole(Role role);
}
