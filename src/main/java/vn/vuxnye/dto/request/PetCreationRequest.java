package vn.vuxnye.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.vuxnye.common.Gender;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class PetCreationRequest implements Serializable {
    @NotBlank(message = "First name must be not blank")
    private String name;

    private Long ownerId;

    private String species;

    private String breed;

    private String color;

    private Gender sex;

    private LocalDate birthDate;


}
