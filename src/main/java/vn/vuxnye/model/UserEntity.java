package vn.vuxnye.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.vuxnye.common.Gender;
import vn.vuxnye.common.UserStatus;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity implements UserDetails, Serializable {

    @Column(name = "first_name",length = 255)
    private String firstName;

    @Column(name = "last_name",length = 255)
    private String lastName;

    @Column(name = "email",length = 255)
    private String email;

    @Column(name = "phone",length = 15)
    private String phone;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "username", nullable = false,unique = true,length = 255)
    private String username;

    @Column(name = "password",length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",length = 255)
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AddressEntity> addresses;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PetEntity> pets;

    @OneToMany(mappedBy = "customer")
    private Set<OrderEntity> orders;

    @OneToMany(mappedBy = "customer")
    private Set<AppointmentEntity> appointmentsAsCustomer;

    @OneToMany(mappedBy = "staff")
    private Set<AppointmentEntity> appointmentsAsStaff;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<RoleEntity> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.ACTIVE.equals(status);
    }
}
