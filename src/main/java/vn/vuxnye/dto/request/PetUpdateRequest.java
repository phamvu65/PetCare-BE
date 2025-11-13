package vn.vuxnye.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import vn.vuxnye.common.Gender;

import java.util.Date;
import java.util.List;

@Getter
@ToString
public class PetUpdateRequest implements java.io.Serializable {
    @NotNull(message = "Id must be not null")
    @Min(value = 1,message ="UserId must be equals or greater than 1"  )
    private Long id;

    @NotBlank(message = "First name must be not blank")
    private String firstName;

    @NotBlank(message = "Last name must be not blank")
    private String lastName;
    private Gender gender;
    private Date birthday;
    private String userName;

    @Email(message = "Email invalid")
    private String email;
    private String phone;
    private List<AddressRequest> addresses;
}
