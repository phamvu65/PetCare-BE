package vn.vuxnye.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vn.vuxnye.common.Gender;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetEntity implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "name",length = 255)
    private String name;

    @Column(name = "species",length = 255)
    private String species;

    @Column(name = "breed",length = 255)
    private String breed;

    @Column(name = "color",length = 255)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex",length = 255)
    private Gender sex;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(mappedBy = "pet")
    private Set<AppointmentEntity> appointments;

}
