package bda.Clinics;

import bda.Clinics.dao.model.Doctor;
import bda.Clinics.dao.model.Review;
import bda.Clinics.dao.repository.DoctorRepository;
import bda.Clinics.dao.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
public class ClinicsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicsApplication.class, args);
	}
}
