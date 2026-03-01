package vn.vuxnye.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Import các Model
import vn.vuxnye.model.PasswordResetToken;
import vn.vuxnye.model.UserEntity;

// Import các Repository
import vn.vuxnye.repository.PasswordResetTokenRepository;
import vn.vuxnye.repository.UserRepository;

// Import Service gửi mail
import vn.vuxnye.service.GmailService;

// Import các thư viện Java
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final GmailService gmailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * API 1: Gửi yêu cầu quên mật khẩu
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Email không tồn tại trong hệ thống");
        }

        // 2. Tạo token ngẫu nhiên
        String token = UUID.randomUUID().toString();

        // 3. Xóa token cũ
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        // 4. Lưu token mới
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();

        tokenRepository.save(resetToken);

        // 5. Gửi mail
        gmailService.sendResetPasswordEmail(email, token);

        return ResponseEntity.ok(Map.of("message", "Link đặt lại mật khẩu đã được gửi vào email!"));
    }

    /**
     * API 2: Đặt lại mật khẩu mới
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        // 1. Tìm token
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ hoặc không tồn tại"));

        // 2. Kiểm tra hết hạn
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            return ResponseEntity.badRequest().body(Map.of("message", "Link đã hết hạn, vui lòng thực hiện lại yêu cầu quên mật khẩu."));
        }

        // 3. Đổi mật khẩu
        UserEntity user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 4. Xóa token sau khi dùng xong
        tokenRepository.delete(resetToken);

        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại."));
    }
}