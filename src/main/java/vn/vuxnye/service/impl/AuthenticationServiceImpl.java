package vn.vuxnye.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.vuxnye.common.TokenType;
import vn.vuxnye.dto.request.SignInRequest;
import vn.vuxnye.dto.response.TokenResponse;
import vn.vuxnye.exception.InvalidDataException;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.TokenEntity;
import vn.vuxnye.repository.TokenRepository;
import vn.vuxnye.repository.UserRepository;
import vn.vuxnye.service.AuthenticationService;
import vn.vuxnye.service.TokenService;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtService;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;


    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Get access token");

        // 1. Xác thực (AuthenticationManager đã gọi UserServiceDetail để check username/email rồi)
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            log.error("Login failed, error:{}", e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }

        // 2. Lấy thông tin User từ DB
        // 🔴 ĐÃ SỬA: Tìm bằng cả Username HOẶC Email để tránh lỗi "User not found"
        var user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Tạo token
        // 🟢 LƯU Ý: Dùng user.getUsername() (tên chuẩn trong DB) thay vì request.getUsername() (input người dùng nhập)
        // Để token luôn chứa username chuẩn, dù họ đăng nhập bằng email.
        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getAuthorities()
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getUsername(),
                user.getAuthorities()
        );

        // 4. Lưu token vào DB
        tokenService.save(
                TokenEntity.builder()
                        .username(user.getUsername()) // Lưu username chuẩn
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .platform(request.getPlatform())
                        .deviceToken(request.getDeviceToken())
                        .versionApp(request.getVersionApp())
                        .build()
        );

        // 5. Trả về kết quả
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }


    @Override
    public TokenResponse getRefreshToken(String refreshToken ) {
        log.info("Get refresh token");
        if(!StringUtils.hasText(refreshToken )){
            log.error("Refresh token is missing or emty");
            throw new InvalidDataException("Refresh token is missing");
        }

        final String username;
        try {
            username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);
        }catch (Exception e){
            log.error("Refresh token is invalid, error:{}", e.getMessage());
            throw new AccessDeniedException("Invalid or expired refresh token", e);
        }

        // Refresh token thì tìm bằng username là đúng, vì token đã chứa username chuẩn
        var user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));;
        if (user == null) {
            log.warn("User not found for refresh token (username: {})", username);
            throw new UsernameNotFoundException("User associated with refresh token not found");
        }

        // create new access token
        String newAccessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getUsername(),
                user.getAuthorities()
        );

        TokenEntity existingToken = tokenRepository.findByRefreshToken(refreshToken).orElseThrow();
        existingToken.setAccessToken(newAccessToken);
        tokenService.save(existingToken);


        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }
}