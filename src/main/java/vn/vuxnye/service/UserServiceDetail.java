package vn.vuxnye.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.vuxnye.repository.UserRepository;

@Service
public record UserServiceDetail(UserRepository userRepository) {

    // Giữ nguyên tên hàm này để các chỗ khác gọi không bị lỗi
    public UserDetailsService UserServiceDetail() {
        return username -> userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}