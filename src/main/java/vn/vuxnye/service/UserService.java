package vn.vuxnye.service;


import vn.vuxnye.dto.request.UserCreationRequest;
import vn.vuxnye.dto.request.UserPasswordRequest;
import vn.vuxnye.dto.request.UserUpdateRequest;
import vn.vuxnye.dto.response.UserPageResponse;
import vn.vuxnye.dto.response.UserResponse;

public interface UserService {

    UserPageResponse findAll(String keyword, String sort, int page, int size);

    UserResponse findById(Long id);

    UserResponse findByUsername(String username);

    UserResponse findByEmail(String email);

    Long save(UserCreationRequest req);

    void update(UserUpdateRequest req);

    void changePassword(UserPasswordRequest req);

    void delete(Long id);

}
