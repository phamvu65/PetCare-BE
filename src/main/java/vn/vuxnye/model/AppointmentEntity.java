package vn.vuxnye.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vuxnye.common.AppointmentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "appointments")
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private PetEntity pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private UserEntity staff;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

}
