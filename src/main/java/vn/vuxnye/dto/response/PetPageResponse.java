package vn.vuxnye.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetPageResponse extends PageResponseAbstract {
    private List<PetResponse> pets;

}
