package com.Alcura.Admin.Repository;

import com.Alcura.Admin.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Integer>
{
    Admin findByEmail (String email);
    Admin findByEmailAndStatus (String email, int status);
    Optional<Admin> findTopByOrderByIdDesc();
    List<Admin> findByFaceDataIsNotNull();
}
