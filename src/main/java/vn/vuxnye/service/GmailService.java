package vn.vuxnye.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GmailService {

    private final JavaMailSender javaMailSender;

    // Lấy email người gửi từ file properties
    @Value("${spring.mail.username}")
    private String senderEmail;

    // Link Frontend (để người dùng bấm vào quay lại trang web)
    // Bạn nhớ thay đổi port nếu React của bạn chạy port khác (ví dụ 3000)
    private final String frontendUrl = "http://localhost:5173";

    @Async // Chạy ngầm để không làm đơ giao diện
    public void sendResetPasswordEmail(String toEmail, String token) {
        log.info("Đang gửi mail reset pass tới: {}", toEmail);

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        String subject = "Yêu cầu đặt lại mật khẩu - PetCare";

        // Nội dung HTML cho đẹp
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; max-width: 600px;">
                <h2 style="color: #4F46E5;">Xin chào,</h2>
                <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản PetCare của bạn.</p>
                <p>Vui lòng nhấn vào nút bên dưới để tạo mật khẩu mới (Link hết hạn sau 15 phút):</p>
                <a href="%s" style="background-color: #4F46E5; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">Đặt lại mật khẩu</a>
                <p style="margin-top: 20px;">Hoặc copy link này: <br> %s</p>
                <p style="color: #999; font-size: 12px;">Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>
            </div>
            """, resetLink, resetLink);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = gửi dạng HTML

            javaMailSender.send(message);
            log.info("Gửi mail thành công!");

        } catch (MessagingException e) {
            log.error("Lỗi khi gửi mail: {}", e.getMessage());
        }
    }
}