package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.dto.request.UserCreationRequest;
import vn.vuxnye.dto.request.UserPasswordRequest;
import vn.vuxnye.dto.request.UserUpdateRequest;
import vn.vuxnye.dto.response.UserResponse;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mockup/user")
@Tag(name = "Mockup User Controller")
public class MockupUserController {
    @Operation(summary = "Get user list",description = "API retrieve user from db")
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(1L);
        userResponse1.setFistName("Vu");
        userResponse1.setLastName("Nguyen");
        userResponse1.setEmail("percare@shop.com");
        userResponse1.setPhone("0909090909");
        userResponse1.setUserName("admin");

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(2L);
        userResponse2.setFistName("Vu");
        userResponse2.setLastName("Nguyen Pham");
        userResponse2.setEmail("percare@shop.com");
        userResponse2.setPhone("090909089");
        userResponse2.setUserName("user");

        List<UserResponse> userList = List.of(userResponse1, userResponse2);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User list");
        result.put("data", userList);
        return result;
    }

    @Operation(summary = "Get user detail",description = "API retrieve user detail by id")
    @GetMapping("/{userId}")
    public Map<String, Object> getList(@PathVariable Long userId) {
        UserResponse userDetail = new UserResponse();
        userDetail.setId(1L);
        userDetail.setFistName("Vu");
        userDetail.setLastName("Nguyen");
        userDetail.setEmail("percare@shop.com");
        userDetail.setPhone("0909090909");
        userDetail.setUserName("admin");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User");
        result.put("data", userDetail);
        return result;
    }

    @Operation(summary = "Create User",description = "API add new user to db")
    @PostMapping("/add")
    public Map<String, Object> createUser(UserCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User created successfully");
        result.put("data",3);
        return result;
    }

    @Operation(summary = "Update User",description = "API update user to db")
    @PutMapping("/upd")
    public Map<String, Object> updateUser(UserUpdateRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User updated successfully");
        result.put("data","");
        return result;
    }

    @Operation(summary = "Change Password",description = "API change password for user to db")
    @PatchMapping("/change-pwd") //id da co trong request r nen k can bo trong url
    public Map<String, Object> changePassword(UserPasswordRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "Password updated successfully");
        result.put("data","");
        return result;
    }

    @Operation(summary = "Delete User",description = "API activate user to db")
    @DeleteMapping("/del/{userId}")
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "User deleted successfully");
        result.put("data","");
        return result;
    }
}
