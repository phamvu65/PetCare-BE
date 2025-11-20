package vn.vuxnye.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "token")
public class TokenEntity extends BaseEntity { // Kế thừa BaseEntity

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;


    @Column(name = "platform", length = 50)
    private String platform; // web, mobile, miniApp

    @Column(name = "device_token", columnDefinition = "TEXT")
    private String deviceToken; // Token của Firebase/Apple Push

    @Column(name = "version_app", length = 20)
    private String versionApp;

}