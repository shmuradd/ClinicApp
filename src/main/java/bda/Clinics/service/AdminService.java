package bda.Clinics.service;

import bda.Clinics.dao.model.User;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    List<User> getAllAdmins();
    Optional<User> getAdminById(Long adminId);
    User saveAdmin(User admin);
    void deleteAdmin(Long adminId);
    User updateAdmin(Long adminId, User admin);
}
