package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.vuxnye.common.ResponseAPI;
import vn.vuxnye.common.UserStatus;
import vn.vuxnye.dto.request.UserCreationRequest;
import vn.vuxnye.dto.request.UserPasswordRequest;
import vn.vuxnye.dto.request.UserUpdateRequest;
import vn.vuxnye.dto.response.UserPageResponse;
import vn.vuxnye.dto.response.UserResponse;
import vn.vuxnye.service.UserService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users", description = "API retrieve all users from db")


    @GetMapping("/list")
    public ResponseAPI getList(
            @RequestParam(required = false) String keyword ,
            @RequestParam(required = false) UserStatus status ,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("get user list. Sort: {}, Page: {}, Size: {}", sort, page, size);
        UserPageResponse pageResponse = userService.findAll(keyword,roleId,status,sort,page,size);

        return ResponseAPI.builder()
                .data(pageResponse)
                .status(HttpStatus.OK)
                .message("Get user list successfully")
                .build();
    }
    @Operation(summary = "Create User",description = "API add new user to db")
    @PostMapping("/add")
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User created successfully");
        result.put("data",userService.save(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }


    @Operation(summary = "Update User",description = "API update user to db")
    @PutMapping("/upd")
    public ResponseAPI updateUser(@RequestBody @Valid UserUpdateRequest request) {
        userService.update(request);

        return ResponseAPI.builder()
                .status(HttpStatus.ACCEPTED)
                .data("")
                .message("User updated successfully")
                .build();
    }

    @DeleteMapping("/del/{userId}")
    public ResponseAPI deleteUser(@PathVariable Long userId ) {
        userService.delete(userId);

        return ResponseAPI.builder()
                .status(HttpStatus.NO_CONTENT)
                .data("")
                .message("User deleted successfully")
                .build();
    }

    @PatchMapping("/restore/{userId}")
    public ResponseAPI restoreUser(@PathVariable Long userId) {
        userService.restore(userId) ;

        return ResponseAPI.builder()
                .status(HttpStatus.OK)
                .data("")
                .message("User restored successfully")
                .build();
    }
    @Operation(summary = "Get user detail ", description = "API retrieve user detail by id")
    @GetMapping("/{userId}")
    public ResponseAPI getUserDetail(@PathVariable @Min(value = 1, message = "UserId must be equals or greater than 1") Long userId) {
        log.info(" get user detail:{} ", userId);
        UserResponse userDetail = userService.findById(userId);
        return ResponseAPI.builder()
                .message("Get user detail successfully")
                .data(userDetail)
                .status(HttpStatus.OK)
                .build();
     }

    @Operation(summary = "Change Password", description = "API change password for user")
    @PatchMapping("/change-pwd")
    public ResponseAPI changePassword(@RequestBody @Valid UserPasswordRequest request) {
        log.info("change password: {}", request);
        userService.changePassword(request);
        return ResponseAPI.builder()
                .message("Change password successfully")
                .data("")
                .build();
    }
}