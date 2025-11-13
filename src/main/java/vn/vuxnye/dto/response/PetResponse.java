package vn.vuxnye.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetResponse {
    private Long id;
    private String name;
    private String species;
    private Integer age;
    private Double weight;
    private String notes;
}
