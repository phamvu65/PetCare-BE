package vn.vuxnye.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentPageResponse extends PageResponseAbstract{
    private List<AppointmentResponse> appointments;
}
