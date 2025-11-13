package vn.vuxnye.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
public class UserPasswordRequest implements Serializable{
    @NotNull(message = "Id must be not null")
    @Min(value = 1,message ="UserId must be equals or greater than 1"  )
    private Long id;

    @NotBlank(message = "Old password must be not blank")
    private String oldPassword;

    @NotBlank(message = "Password must be not blank")
    private String password;

    @NotBlank(message = "Confirm password must be not blank")
    private String confirmPassword;


}
