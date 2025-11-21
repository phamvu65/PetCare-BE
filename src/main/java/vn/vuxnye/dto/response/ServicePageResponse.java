package vn.vuxnye.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePageResponse extends PageResponseAbstract{
    private List<ServiceResponse> services;
}
