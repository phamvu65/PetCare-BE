package vn.vuxnye.dto.request;

import lombok.Getter;

@Getter

public class SignInRequest implements java.io.Serializable{
    private String username;
    private String password;
    private String platform; // web, mobile, miniApp
    private String deviceToken;
    private String versionApp;

}
