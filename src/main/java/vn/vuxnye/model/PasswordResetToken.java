package vn.vuxnye.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token ngẫu nhiên (sẽ gửi qua email)
    @Column(nullable = false, unique = true)
    private String token;

    // Token này thuộc về User nào?
    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;

    // Thời gian hết hạn
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // Hàm kiểm tra xem token còn hạn không
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}