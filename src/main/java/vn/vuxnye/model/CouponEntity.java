package vn.vuxnye.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vuxnye.common.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;


    @Enumerated(EnumType.STRING)
    private CouponType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @Column(name = "min_order_value", precision = 15, scale = 2)
    private BigDecimal minOrderValue;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "coupon")
    private Set<OrderCouponEntity> applications;

}
