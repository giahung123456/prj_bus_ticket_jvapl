package com.example.bus_ticket_prj_jvapl.controller;

import com.example.bus_ticket_prj_jvapl.model.dto.UserRegistrationDTO;
import com.example.bus_ticket_prj_jvapl.model.entity.User;
import com.example.bus_ticket_prj_jvapl.repository.TripRepository;
import com.example.bus_ticket_prj_jvapl.repository.UserRepository;
import com.example.bus_ticket_prj_jvapl.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserRepository userRepository;
    @Autowired private TripRepository tripRepository;
    // 1. Vào trang chủ mặc định
    @GetMapping("/")
    public String index(HttpSession session,Model model) {
        User user = (User) session.getAttribute("loggedInUser");
//        if (user == null) return "redirect:/login";
        loadSearchData(model);
        if (user == null) {

            return "passenger/home";
        }
        // Điều hướng dựa trên Role nếu đã đăng nhập
        if ("ADMIN".equals(user.getRole().name())) return "redirect:/admin/index";
        if ("STAFF".equals(user.getRole().name())) return "redirect:/staff/tickets";
        return "passenger/home";
    }
    private void loadSearchData(Model model) {
        model.addAttribute("departures", tripRepository.findAllDeparturePoints());
        model.addAttribute("destinations", tripRepository.findAllDestinationPoints());
    }
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        // 1. Kiểm tra để trống thủ công vì login dùng @RequestParam
        if (username.isBlank() || password.isBlank()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ tài khoản và mật khẩu");
            return "auth/login";
        }
        User user = userRepository.findByUsername(username);

        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            session.setAttribute("loggedInUser", user);
            // Redirect về trang chủ để index() tự điều hướng theo Role
            return "redirect:/";
        }

        model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
        return "auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDTO dto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        // 1. Kiểm tra trùng tên đăng nhập trước (ngay cả khi các ô khác trống)
        try {
            // Ta có thể gọi một hàm checkUsername riêng hoặc để try-catch bao quanh toàn bộ
            userService.checkDuplicateUsername(dto.getUsername());
        } catch (RuntimeException e) {
            result.rejectValue("username", "duplicate", e.getMessage());
        }
        if (result.hasErrors()) return "auth/register";
        try {
            userService.registerPassenger(dto);
            redirectAttributes.addFlashAttribute("successMsg", "Đăng ký thành công!");
            return "redirect:/login";
        } catch (RuntimeException e) {
//            result.rejectValue("username", "error.user", e.getMessage());
            result.rejectValue("username", "duplicate", e.getMessage());
            return "auth/register";
        }
    }
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        // StringTrimmerEditor(true) sẽ cắt khoảng trắng đầu cuối.
        // Nếu truyền true, chuỗi chỉ có dấu cách sẽ được chuyển thành null.
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }
}