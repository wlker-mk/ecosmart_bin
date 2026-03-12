package com.ecosmart.ecosmart_bin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private int points;
    private String role; // USER, ADMIN, AGENT
    private String telephone;

    // ✅ AJOUT 1 — numéro de carte étudiante (optionnel, unique en base)
    // Permet la connexion par carte au lieu de l'email
    // Exemple : "ETU-2024-00123"
    @Column(unique = true)
    private String carteEtudiante;

    // ✅ AJOUT 2 — flag étudiant vérifié
    // Si true → les points sont multipliés x2 à chaque dépôt
    private boolean estEtudiant;
}