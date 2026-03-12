package com.ecosmart.ecosmart_bin.dto;

import com.ecosmart.ecosmart_bin.entity.User;
import lombok.Data;

// ✅ BUG CORRIGÉ — avant on retournait l'entité User directement
// → le password était exposé dans toutes les réponses API
// → maintenant on passe par ce DTO qui exclut le password
@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private int points;
    private String role;
    private String carteEtudiante; // ✅ AJOUT
    private boolean estEtudiant;   // ✅ AJOUT

    public static UserDTO from(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setTelephone(user.getTelephone());
        dto.setPoints(user.getPoints());
        dto.setRole(user.getRole());
        dto.setCarteEtudiante(user.getCarteEtudiante()); // ✅ AJOUT
        dto.setEstEtudiant(user.isEstEtudiant());         // ✅ AJOUT
        return dto;
    }
}