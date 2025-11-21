package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.dto.request.UserCreationRequest;
import vn.vuxnye.dto.request.UserPasswordRequest;
import vn.vuxnye.dto.request.UserUpdateRequest;
import vn.vuxnye.dto.response.UserPageResponse;
import vn.vuxnye.dto.response.UserResponse;
import vn.vuxnye.service.UserService;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user list",description = "API retrieve user from db")
    @GetMapping("/list")
    public Map<String,Object> getList(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        log.info("get user list");

        UserPageResponse pageResponse = userService.findAll(keyword, sort, page, size);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user List");
        result.put("data", pageResponse);

        return result;
    }

    @Operation(summary = "Get user detail",description = "API retrieve user detail by id")
    @GetMapping("/{userId}")
    public Map<String, Object> getUserDetail(@PathVariable @Min(value = 1,message ="UserId must be equals or greater than 1") Long userId) {
        log.info("get user detail:{}", userId);

        UserResponse userDetail= userService.findById(userId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User");
        result.put("data", userDetail);
        return result;
    }

    @Operation(summary = "Create User",description = "API add new user to db")
    @PostMapping("/add")
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("create user:{}", request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User created successfully");
        result.put("data",userService.save(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Update User",description = "API update user to db")
    @PutMapping("/upd")
    public Map<String, Object> updateUser(@RequestBody @Valid UserUpdateRequest request) {
        log.info("update user:{}", request);

        userService.update(request);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User updated successfully");
        result.put("data","");
        return result;
    }

    @Operation(summary = "Change Password",description = "API change password for user to db")
    @PatchMapping("/change-pwd") //id da co trong request r nen k can bo trong url
    public Map<String, Object> changePassword(@RequestBody @Valid  UserPasswordRequest request) {
        log.info("change password:{}", request);
        userService.changePassword(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "Password updated successfully");
        result.put("data","");
        return result;
    }

    @Operation(summary = "Delete User",description = "API activate user to db")
    @DeleteMapping("/del/{userId}")
    public Map<String, Object> deleteUser(@PathVariable @Min(value = 1,message ="UserId must be equals or greater than 1") Long userId) {
        log.info("delete user:{}", userId);
        userService.delete(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User deleted successfully");
        result.put("data","");
        return result;
    }

    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String secretCode, HttpServletResponse response) throws IOException {
        log.info("confirm email:{}", secretCode);
        try {
            // TODO check or compare secretCode from database
        }catch (Exception e){
            log.error("Confirm email was failure!,errorMessage={}", e.getMessage());
        }finally {
            response.sendRedirect("https://petcare.vn/wp-admin");
        }

    }
}
