package com.ecosmart.ecosmart_bin.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String telephone;

    // ✅ AJOUT — optionnel, si fourni → compte étudiant + 10 pts bonus
    private String carteEtudiante;
}