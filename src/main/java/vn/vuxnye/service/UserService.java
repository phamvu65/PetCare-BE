package vn.vuxnye.service;

import vn.vuxnye.common.UserStatus;
import vn.vuxnye.dto.request.UserCreationRequest;
import vn.vuxnye.dto.request.UserPasswordRequest;
import vn.vuxnye.dto.request.UserUpdateRequest;
import vn.vuxnye.dto.response.UserPageResponse;
import vn.vuxnye.dto.response.UserResponse;

public interface UserService {

    UserPageResponse findAll(String keyword, Long roleId, UserStatus status, String sort, int page, int size);

    UserResponse findById(Long id);

    UserResponse findByUsername(String username);

    Long save(UserCreationRequest req);

    void update(UserUpdateRequest req);

    void changePassword(UserPasswordRequest req);

    void delete(Long id);

    void restore(Long id);
}