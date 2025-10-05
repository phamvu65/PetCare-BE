package vn.vuxnye;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetCareApplication {

	@Value("${jwt.secretKey}")
	private String jwtKey;
	public static void main(String[] args) {
		SpringApplication.run(PetCareApplication.class, args);
	}

	@PostConstruct
	public void test() {
		System.out.println("JWT Secret Key: " + jwtKey);
	}
}
