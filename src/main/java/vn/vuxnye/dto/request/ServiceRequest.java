package vn.vuxnye.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServiceRequest {

    @NotBlank(message = "Service name must not be blank")
    private String name;

    private String description;

    private String imageUrl;

    @NotNull(message = "Price must not be null")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    @NotNull(message = "Duration must not be null")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMin;

    private Boolean active = true; // Default is active
}