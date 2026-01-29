package com.grandvista.backend.repository;

import com.grandvista.backend.model.StaffUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffUserRepository extends MongoRepository<StaffUser, String> {
    Optional<StaffUser> findByEmail(String email);
}
