package vn.vuxnye.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import vn.vuxnye.common.Gender;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@ToString
public class PetUpdateRequest implements java.io.Serializable {
    @NotNull(message = "Id must be not null")
    @Min(value = 1,message ="UserId must be equals or greater than 1"  )
    private Long id;

    private Long ownerId;

    private String name;

    private String species;

    private String breed;

    private String color;

    private Gender sex;

    private LocalDate birthDate;


}
