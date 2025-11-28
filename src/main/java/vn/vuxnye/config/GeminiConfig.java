package vn.vuxnye.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GeminiConfig {

    @Bean("geminiRestTemplate")
    public RestTemplate geminiRestTemplate() {
        return new RestTemplate();
    }
}