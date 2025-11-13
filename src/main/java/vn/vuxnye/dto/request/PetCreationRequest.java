package vn.vuxnye.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PetCreationRequest implements Serializable {
    @NotBlank(message = "First name must be not blank")
    private String name;

    private String species;

    private Integer age;

    private Double weight;

    private String notes;
}
