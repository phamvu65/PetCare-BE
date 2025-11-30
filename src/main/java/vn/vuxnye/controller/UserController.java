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

    @Operation(summary = "Get user list", description = "API retrieve user from db with filters")
    @GetMapping("/list")
    public Map<String,Object> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) Long roleId, // 🟢 THÊM: Lọc theo Role ID
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("get user list. Role: {}, Status: {}", roleId, status);

        UserPageResponse pageResponse = userService.findAll(keyword, roleId, status, sort, page, size);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user List");
        result.put("data", pageResponse);

        return result;
    }

    // ... Các API khác giữ nguyên (add, upd, del, restore) ...
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
    public Map<String, Object> updateUser(@RequestBody @Valid UserUpdateRequest request) {
        userService.update(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User updated successfully");
        result.put("data","");
        return result;
    }

    @DeleteMapping("/del/{userId}")
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User locked successfully");
        result.put("data","");
        return result;
    }

    @PatchMapping("/restore/{userId}")
    public Map<String, Object> restoreUser(@PathVariable Long userId) {
        userService.restore(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User restored successfully");
        result.put("data", "");
        return result;
    }
}