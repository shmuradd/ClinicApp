package bda.Clinics;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class ClinicsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicsApplication.class, args);
	}
}
