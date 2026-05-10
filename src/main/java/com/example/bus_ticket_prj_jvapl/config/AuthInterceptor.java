package com.example.bus_ticket_prj_jvapl.config;

import com.example.bus_ticket_prj_jvapl.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute("loggedInUser");

        if (user == null) {
            response.sendRedirect("/login"); // Chưa login thì về trang chủ
            return false;
        }

        // Kiểm tra quyền Admin
        String uri = request.getRequestURI();
        if (uri.startsWith("/admin") && !user.getRole().name().equals("ADMIN")) {
            response.sendRedirect("/access-denied"); // Sai quyền thì chặn
            return false;
        }
// Nếu vào trang staff mà không phải role STAFF/ADMIN thì chặn
//        if (uri.startsWith("/staff") && !user.getRole().name().equals("STAFF") && !user.getRole().name().equals("ADMIN")) {
//            response.sendRedirect("/access-denied");
//            return false;
//        }
        
        // Trong AuthInterceptor.java


// Đảm bảo /staff hoặc /staff/ đều bị kiểm tra
        if (uri.equals("/staff") || uri.startsWith("/staff/")) {
            if (!user.getRole().name().equals("STAFF") && !user.getRole().name().equals("ADMIN")) {
                response.sendRedirect("/access-denied");
                return false;
            }
        }
        return true;
    }
}