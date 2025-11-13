package vn.vuxnye.service;

import vn.vuxnye.dto.request.SignInRequest;
import vn.vuxnye.dto.response.TokenResponse;

public interface AuthenticationService {
    TokenResponse getAccessToken(SignInRequest request);
    TokenResponse getRefreshToken(String token);
}
