package vn.vuxnye.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import vn.vuxnye.common.Role;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class RoleEntity implements java.io.Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;
}
