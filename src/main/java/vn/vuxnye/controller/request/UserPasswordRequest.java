package vn.vuxnye.controller.request;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
public class UserPasswordRequest implements Serializable{
    private Long id;
    private String password;
    private String confirmPassword;


}
