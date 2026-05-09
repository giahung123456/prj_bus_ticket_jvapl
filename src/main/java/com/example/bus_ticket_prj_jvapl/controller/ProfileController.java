package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.entity.User;
import com.example.bus_ticket_prj_jvapl.model.entity.UserProfile;
import com.example.bus_ticket_prj_jvapl.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired private UserService userService;

    @GetMapping
    public String showProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        // Lấy profile từ user
        UserProfile profile = user.getProfile();

        // NẾU PROFILE ĐANG NULL (do user mới chưa cập nhật bao giờ)
        if (profile == null) {
            profile = new UserProfile();
        }

        // Gửi đối tượng "profile" này sang HTML
        model.addAttribute("profile", profile);
        return "profile/index";
    }

    @PostMapping("/update")
    public String updateProfile(com.example.bus_ticket_prj_jvapl.model.entity.UserProfile profile,
                                HttpSession session,
                                RedirectAttributes ra) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";

        try {
            userService.updateProfile(user.getUsername(), profile);

            // Cập nhật lại dữ liệu mới vào Session để hiển thị ngay lập tức
            user.setProfile(profile);
            session.setAttribute("loggedInUser", user);

            ra.addFlashAttribute("successMsg", "Cập nhật thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}