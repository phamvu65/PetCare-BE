package vn.vuxnye.dto.response;

import lombok.*;
import vn.vuxnye.common.Gender;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String fistName;
    private String lastName;
    private String userName;
    private String email;
    private String phone;
}
