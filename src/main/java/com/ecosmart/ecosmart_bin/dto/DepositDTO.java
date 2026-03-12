package com.ecosmart.ecosmart_bin.dto;

import com.ecosmart.ecosmart_bin.entity.Deposit;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO pour retourner un dépôt sans boucle infinie JSON.
 * Deposit -> User -> List<Deposit> -> User -> ... = CRASH
 * On coupe la boucle en ne retournant que les IDs des relations.
 */
@Data
public class DepositDTO {
    private Long id;
    private double poids;
    private int pointsGagnes;
    private LocalDateTime dateDepot;
    private Long userId;
    private String userNom;
    private Long binId;
    private String binNom;

    public static DepositDTO from(Deposit deposit) {
        DepositDTO dto = new DepositDTO();
        dto.setId(deposit.getId());
        dto.setPoids(deposit.getPoids());
        dto.setPointsGagnes(deposit.getPointsGagnes());
        dto.setDateDepot(deposit.getDateDepot());
        dto.setUserId(deposit.getUser().getId());
        dto.setUserNom(deposit.getUser().getNom());
        dto.setBinId(deposit.getBin().getId());
        dto.setBinNom(deposit.getBin().getNom());
        return dto;
    }
}
