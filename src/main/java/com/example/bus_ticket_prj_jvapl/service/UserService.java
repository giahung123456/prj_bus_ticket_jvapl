package com.example.bus_ticket_prj_jvapl.service;

import com.example.bus_ticket_prj_jvapl.model.dto.UserRegistrationDTO;
import com.example.bus_ticket_prj_jvapl.model.entity.User;
import com.example.bus_ticket_prj_jvapl.model.entity.UserProfile;
import com.example.bus_ticket_prj_jvapl.model.entity.enums.Role;
import com.example.bus_ticket_prj_jvapl.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // Thay BCryptPasswordEncoder bằng PasswordEncoder
//
    // 1. Logic Đăng ký cho Passenger (CORE-01)
@Transactional(rollbackFor = Exception.class) // Đảm bảo rollback nếu có bất kỳ lỗi nào
public void registerPassenger(UserRegistrationDTO dto) {
    // 1. Kiểm tra xem tên đăng nhập đã tồn tại chưa (Tránh lỗi Duplicate)
    if (userRepository.findByUsername(dto.getUsername()) != null) {
        throw new RuntimeException("Tên đăng nhập '" + dto.getUsername() + "' đã tồn tại!");
    }
// 2. KIỂM TRA SỐ ĐIỆN THOẠI (MỚI THÊM)
    if (userRepository.findByPhoneNumber(dto.getPhoneNumber()) != null) {
        throw new RuntimeException("Số điện thoại '" + dto.getPhoneNumber() + "' đã được sử dụng bởi tài khoản khác!");
    }
    // 2. Kiểm soát truy cập: Luôn gán Role là PASSENGER cho luồng đăng ký công khai
    // Ngay cả khi DTO gửi lên Role khác, chúng ta vẫn ghi đè để bảo mật
    Role finalRole = Role.PASSENGER;

    // 3. Tạo Profile trước bằng Builder
    UserProfile profile = UserProfile.builder()
            .fullName(dto.getFullName())
            .phoneNumber(dto.getPhoneNumber())
            .email(dto.getEmail())
            .build();

    // 4. Tạo User bằng Builder (Sử dụng BCrypt để băm mật khẩu theo CORE-01)
    User user = User.builder()
            .username(dto.getUsername())
            .passwordHash(passwordEncoder.encode(dto.getPassword()))
            .role(finalRole)
            .profile(profile)
            .build();

    // 5. Thiết lập mối quan hệ ngược (Bắt buộc cho @OneToOne bi-directional)
    profile.setUser(user);

    // 6. Lưu xuống DB - Nhờ CascadeType.ALL ở Entity User, Profile sẽ được lưu theo
    userRepository.save(user);
}

    @PostConstruct
    public void initData() {
        initAdmins();
        initStaffs(); // Thêm hàm khởi tạo nhân viên
    }
    private void initAdmins() {
        if (userRepository.findByRole(Role.ADMIN).isEmpty()) {
            String[][] adminData = {
                    {"admin_master", "Admin@123", "Nguyễn Văn Admin"},
                    {"admin_support", "Support@2026", "Lê Quản Trị"},
                    {"admin_phuong", "Phuong@678", "Trần Minh Phương"}
            };
            createUsers(adminData, Role.ADMIN);
        }
    }

    private void initStaffs() {
        // Kiểm tra nếu chưa có nhân viên nào thì mới tạo (CORE-04)
        if (userRepository.findByRole(Role.STAFF).isEmpty()) {

            // Danh sách nhân viên mẫu
            String[][] staffData = {
                    {"staff_01", "Staff@123", "Nguyễn Thị Thanh"},
                    {"staff_02", "Staff@123", "Trần Văn Duy"},
                    {"staff_03", "Staff@123", "Hoàng Thu Thủy"}
            };

            createUsers(staffData, Role.STAFF);
            System.out.println(">>> DataSeed: Đã khởi tạo danh sách Nhân viên mẫu.");
        }
    }

    // Hàm dùng chung để tạo User và Profile bằng Builder
    private void createUsers(String[][] dataList, Role role) {
        for (String[] data : dataList) {
            User user = User.builder()
                    .username(data[0])
                    .passwordHash(passwordEncoder.encode(data[1])) // Băm mật khẩu (CORE-01)
                    .role(role)
                    .build();

            UserProfile profile = UserProfile.builder()
                    .fullName(data[2])
                    .phoneNumber("03" + (int)(Math.random() * 100000000))
                    .user(user)
                    .build();

            user.setProfile(profile);
            userRepository.save(user);
        }
    }
    @Transactional(readOnly = true)
    public UserProfile getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new RuntimeException("Người dùng không tồn tại");
        return user.getProfile();
    }

    @Transactional
    public void updateProfile(String username, UserProfile updatedData) {
        User user = userRepository.findByUsername(username);
        UserProfile profile = user.getProfile();

        // Chỉ cập nhật các trường được phép (CORE-03)
        profile.setFullName(updatedData.getFullName());
        profile.setPhoneNumber(updatedData.getPhoneNumber());
        profile.setEmail(updatedData.getEmail());

        userRepository.save(user);
    }
    public void checkDuplicateUsername(String username) {
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("Tên đăng nhập '" + username + "' đã tồn tại!");
        }
    }
    public void checkDuplicatePhone(String phoneNumber) {
        if (phoneNumber != null && userRepository.findByPhoneNumber(phoneNumber) != null) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }
    }
}