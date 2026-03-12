package com.ecosmart.ecosmart_bin.dto;

import lombok.Data;

// ✅ BUG CORRIGÉ — était une classe vide : public class LoginRequest {}
// Deux modes de connexion possibles :
//  1. email + password       (tout le monde)
//  2. carteEtudiante + password  (étudiants)
// Si carteEtudiante est fourni → priorité sur email
@Data
public class LoginRequest {
    private String email;           // mode standard
    private String carteEtudiante;  // ✅ AJOUT — mode étudiant
    private String password;        // toujours requis
}