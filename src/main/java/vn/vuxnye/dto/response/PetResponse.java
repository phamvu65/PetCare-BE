package vn.vuxnye.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import vn.vuxnye.common.Gender;
import vn.vuxnye.model.PetEntity;
import vn.vuxnye.model.UserEntity;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetResponse {
    private Long id;
    private String owner;
    private String name;
    private String species;
    private String breed;
    private String color;
    private Gender sex;
    private LocalDate birthDate;

    public static PetResponse fromEntity(PetEntity entity) {
        return PetResponse.builder()
                .id(entity.getId())
                .owner(entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName())
                .name(entity.getName())
                .species(entity.getSpecies())
                .breed(entity.getBreed())
                .color(entity.getColor())
                 .sex(entity.getSex())
                .birthDate(entity.getBirthDate())
                .build();
    }
}
