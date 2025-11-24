package vn.vuxnye.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import vn.vuxnye.model.CategoryEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Nếu field nào null thì không trả về trong JSON
public class CategoryDTO {

    // Dùng cho Response (trả về ID), Request có thể null
    private Long id;

    // Dùng cho cả Request (validate) và Response
    @NotBlank(message = "Category name must not be blank")
    private String name;

    // Hàm tiện ích chuyển đổi từ Entity -> DTO
    public static CategoryDTO fromEntity(CategoryEntity entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    // Hàm tiện ích chuyển đổi từ DTO -> Entity (Cho lúc Create)
    public static CategoryEntity toEntity(CategoryDTO dto) {
        return CategoryEntity.builder()
                .name(dto.getName())
                .build();
    }
}