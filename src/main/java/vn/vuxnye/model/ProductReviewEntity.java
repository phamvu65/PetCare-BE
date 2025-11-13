package vn.vuxnye.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_reviews")
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewEntity extends BaseEntity {

    @ManyToOne @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "rating")
    private Byte rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;


}
