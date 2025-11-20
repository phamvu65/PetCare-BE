package vn.vuxnye.service;

import org.springframework.stereotype.Service;
import vn.vuxnye.exception.ResourceNotFoundException;
import vn.vuxnye.model.TokenEntity;
import vn.vuxnye.repository.TokenRepository;

import java.util.Optional;

@Service
public record TokenService(TokenRepository tokenRepository) {

    /**
     * Get token by username
     *
     * @param username
     * @return token
     */
    public TokenEntity getByUsername(String username) {
        return tokenRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Token not found"));
    }

    /**
     * Save token to DB
     *
     * @param token
     * @return
     */
    public long save(TokenEntity token) {
        Optional<TokenEntity> optional = tokenRepository.findByUsername(token.getUsername());
        if (optional.isEmpty()) {
            tokenRepository.save(token);
            return token.getId();
        } else {
            TokenEntity tokenEntity = optional.get();
            tokenEntity.setAccessToken(token.getAccessToken());
            tokenEntity.setRefreshToken(token.getRefreshToken());
            tokenRepository.save(tokenEntity);
            return tokenEntity.getId();
        }
    }

    /**
     * Delete token by username
     *
     * @param username
     */
    public void delete(String username) {
        TokenEntity token = getByUsername(username);
        tokenRepository.delete(token);
    }

}
