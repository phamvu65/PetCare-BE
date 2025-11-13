package vn.vuxnye.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import vn.vuxnye.common.Gender;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@ToString
public class UserCreationRequest implements Serializable{
    @NotBlank(message = "First name must be not blank")
    private String firstName;

    @NotBlank(message = "Last name must be not blank")
    private String lastName;

    @NotBlank(message = "Password must be not blank")
    private String password;

    private String userName;

    @Email(message = "Email invalid")
    private String email;

    private String phone;

    @NotNull(message = "role ID is required")
    @JsonProperty("role_id")
    private long roleId;

    private List<AddressRequest> addresses; //home, office

}
