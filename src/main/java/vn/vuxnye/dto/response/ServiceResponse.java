package vn.vuxnye.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.model.ServiceEntity;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ServiceResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Integer durationMin;
    private Boolean active;

    public static ServiceResponse fromEntity(ServiceEntity entity) {
        return ServiceResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .price(entity.getPrice())
                .durationMin(entity.getDurationMin())
                .active(entity.getActive())
                .build();
    }
}