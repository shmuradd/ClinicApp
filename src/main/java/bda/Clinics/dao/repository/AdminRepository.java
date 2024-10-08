package bda.Clinics.dao.repository;

import bda.Clinics.dao.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<User,Long> {
}
