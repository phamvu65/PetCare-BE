package vn.vuxnye.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;
import vn.vuxnye.model.AppointmentEntity;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentPageResponse extends PageResponseAbstract{
    private List<AppointmentResponse> appointments;
}
