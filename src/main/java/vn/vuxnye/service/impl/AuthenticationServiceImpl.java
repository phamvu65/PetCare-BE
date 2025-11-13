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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.vuxnye.common.TokenType;
import vn.vuxnye.dto.request.SignInRequest;
import vn.vuxnye.dto.response.TokenResponse;
import vn.vuxnye.exception.InvalidDataException;
import vn.vuxnye.model.TokenEntity;
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


    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Get access token");

        //Check db ktra userdetail co hop le
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            log.error("Login failed, error:{}", e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }

        var user = userRepository.findByUsername(request.getUsername());
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }

        String accessToken= jwtService.generateAccessToken(user.getId(),request.getUsername(),user.getAuthorities());
        String refreshToken= jwtService.generateRefreshToken(user.getId(),request.getUsername(),user.getAuthorities());

        //save token to db
        tokenService.save(TokenEntity
                .builder()
                .username(request.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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

        var user = userRepository.findByUsername(username);
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

        // save token to db
        tokenService.save(TokenEntity
                .builder()
                .username(user.getUsername())
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build());


        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();

    }

}
