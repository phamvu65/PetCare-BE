package vn.vuxnye.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageResponse extends PageResponseAbstract implements java.io.Serializable {
    private List<UserResponse> users;
}
