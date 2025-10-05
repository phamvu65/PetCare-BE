package vn.vuxnye.controller.request;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
public class UserUpdateRequest implements Serializable {
    private Long id;
    private String fistName;
    private String lastName;
    private String gender;
    private Date birthday;
    private String userName;
    private String email;
    private String phone;
}
