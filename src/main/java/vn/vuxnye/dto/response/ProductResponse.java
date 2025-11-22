package vn.vuxnye.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.model.ProductEntity;
import vn.vuxnye.model.ProductImageEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class ProductResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private List<String> imageUrls;

    public static ProductResponse fromEntity(ProductEntity entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "Uncategorized")
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                // Convert List<ProductImageEntity> to List<String>
                .imageUrls(entity.getImages().stream()
                        .map(ProductImageEntity::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}