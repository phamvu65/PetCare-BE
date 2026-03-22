package vn.vuxnye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PetCareApplication {
	public static void main(String[] args) {
		SpringApplication.run(PetCareApplication.class, args);
	}

}
