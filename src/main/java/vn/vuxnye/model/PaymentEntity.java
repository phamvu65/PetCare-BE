package vn.vuxnye.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vuxnye.common.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 20)
    private PaymentMethod method;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
