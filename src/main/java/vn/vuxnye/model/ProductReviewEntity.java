package vn.vuxnye.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product_reviews")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewEntity extends BaseEntity {

    @ManyToOne @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "rating")
    private Byte rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    // Tự tham chiếu để tạo luồng trả lời
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProductReviewEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC") // Sắp xếp câu trả lời theo thời gian
    @Builder.Default
    private List<ProductReviewEntity> replies = new ArrayList<>();


}
