package vn.vuxnye.dto.response;

import lombok.*;
import vn.vuxnye.model.ProductEntity;
import vn.vuxnye.model.ProductImageEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;

    // 🟢 SỬA QUAN TRỌNG:
    // Đổi tên biến thành 'images' và kiểu dữ liệu thành List<ProductImageResponse>
    // Để khớp với Frontend: interface Product { images: { id: number; imageUrl: string }[] }
    private List<ProductImageResponse> images;

    public static ProductResponse fromEntity(ProductEntity entity) {
        // 1. Xử lý map danh sách ảnh (Kiểm tra null an toàn)
        List<ProductImageResponse> imgList = new ArrayList<>();
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            imgList = entity.getImages().stream()
                    .map(img -> new ProductImageResponse(img.getId(), img.getImageUrl()))
                    .collect(Collectors.toList());
        }

        // 2. Build response
        return ProductResponse.builder()
                .id(entity.getId())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "Uncategorized")
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .images(imgList) // 🟢 Gán list ảnh object vào đây
                .build();
    }

    // 🟢 Class con (Nested Class) để định nghĩa cấu trúc ảnh
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageResponse {
        private Long id;
        private String imageUrl;
    }
}