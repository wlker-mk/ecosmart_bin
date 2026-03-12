package com.ecosmart.ecosmart_bin.repository;

import com.ecosmart.ecosmart_bin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // ✅ AJOUT — connexion et vérification unicité par carte étudiante
    // Spring Data génère automatiquement le SQL correspondant
    Optional<User> findByCarteEtudiante(String carteEtudiante);
    boolean existsByCarteEtudiante(String carteEtudiante);
}