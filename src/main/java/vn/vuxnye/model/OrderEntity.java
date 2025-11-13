package vn.vuxnye.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vuxnye.common.OrderChannel;
import vn.vuxnye.common.OrderStatus;
import vn.vuxnye.common.PaymentMethod;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends BaseEntity {

    @ManyToOne @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", length = 10)
    private OrderChannel channel = OrderChannel.WEB;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderDetailEntity> orderDetails = new java.util.HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<PaymentEntity> payments;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderCouponEntity> orderCoupons;
}
