package com.ecosmart.ecosmart_bin.dto;

import com.ecosmart.ecosmart_bin.entity.RewardUser;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO pour retourner un échange de récompense sans boucle infinie JSON.
 */
@Data
public class RewardUserDTO {
    private Long id;
    private Long userId;
    private String userNom;
    private Long rewardId;
    private String rewardNom;
    private int pointsDepenses;
    private LocalDateTime dateEchange;
    private String statut;

    public static RewardUserDTO from(RewardUser rewardUser) {
        RewardUserDTO dto = new RewardUserDTO();
        dto.setId(rewardUser.getId());
        dto.setUserId(rewardUser.getUser().getId());
        dto.setUserNom(rewardUser.getUser().getNom());
        dto.setRewardId(rewardUser.getReward().getId());
        dto.setRewardNom(rewardUser.getReward().getNom());
        dto.setPointsDepenses(rewardUser.getReward().getPointsRequis());
        dto.setDateEchange(rewardUser.getDateEchange());
        dto.setStatut(rewardUser.getStatut());
        return dto;
    }
}
