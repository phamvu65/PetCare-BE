package vn.vuxnye.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "recipient_name", nullable = false, length = 255)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    @Column(nullable = false, length = 100)
    private String city; // Tỉnh/Thành phố

    @Column(nullable = false, length = 100)
    private String ward; // Xã/Phường

    @Column(name = "address_detail", nullable = false, length = 255)
    private String addressDetail;

    @Column(name = "is_default", nullable = true)
    private Boolean isDefault;


}
