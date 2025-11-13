package vn.vuxnye.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
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
public class UserUpdateRequest implements Serializable {

    @NotNull(message = "Id must be not null")
    @Min(value = 1,message ="UserId must be equals or greater than 1"  )
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    @Email(message = "Email invalid")
    private String email;
    private String phone;
    private List<AddressRequest> addresses;
}
