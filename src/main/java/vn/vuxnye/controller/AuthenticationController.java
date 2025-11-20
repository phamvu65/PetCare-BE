package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vuxnye.dto.request.SignInRequest;
import vn.vuxnye.dto.response.TokenResponse;
import vn.vuxnye.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
@Tag(name = "Authentication Controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Access token",description = "Get access token and refresh token by usename and password")
    @PostMapping("/access-token")
    public TokenResponse getAccessToken(@RequestBody SignInRequest request) {
        log.info("Get access-token for user: {}", request.getUsername());

        return authenticationService.getAccessToken(request);
    }

    @Operation(summary = "Refresh token",description = "Get new access token by refresh token")
    @PostMapping("/refresh-token")
//    public TokenResponse GetRefreshToken(HttpServletRequest request) {
//        log.info("Refresh token request");
//
//
//        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        final String refreshToken;
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            throw new AccessDeniedException("Refresh token is missing or invalid");
//        }
//
//        refreshToken = authHeader.substring(7);
//
//        return authenticationService.getRefreshToken(refreshToken);
//    }
    public TokenResponse GetRefreshToken(@RequestBody String refreshToken) {
        log.info("Refresh token request");
        return authenticationService.getRefreshToken(refreshToken);
    }


}
