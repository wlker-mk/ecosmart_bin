package com.ecosmart.ecosmart_bin.dto;

import com.ecosmart.ecosmart_bin.entity.User;
import lombok.Data;

/**
 * DTO utilisé pour retourner les données d'un utilisateur dans les réponses API.
 * On n'expose JAMAIS l'entité User directement pour éviter :
 *   - la boucle infinie JSON (User -> Deposit -> User -> ...)
 *   - l'exposition du mot de passe
 */
@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private int points;
    private String role;

    /** Convertit une entité User en DTO */
    public static UserDTO from(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setTelephone(user.getTelephone());
        dto.setPoints(user.getPoints());
        dto.setRole(user.getRole());
        return dto;
    }
}
