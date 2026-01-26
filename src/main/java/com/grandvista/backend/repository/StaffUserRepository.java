package com.grandvista.backend.repository;

import com.grandvista.backend.model.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffUserRepository extends JpaRepository<StaffUser, UUID> {
    Optional<StaffUser> findByEmail(String email);
}
