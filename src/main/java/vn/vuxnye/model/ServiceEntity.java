package vn.vuxnye.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "services")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEntity extends BaseEntity {

    @Column(name = "name",length = 255)
    private String name;

    @Column(name = "description", columnDefinition ="TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_min")
    private Integer durationMin;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "service")
    private Set<AppointmentEntity> appointments;



}
