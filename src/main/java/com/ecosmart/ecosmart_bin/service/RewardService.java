// RewardService.java
package com.ecosmart.ecosmart_bin.service;

import com.ecosmart.ecosmart_bin.entity.Reward;
import com.ecosmart.ecosmart_bin.entity.RewardUser;
import com.ecosmart.ecosmart_bin.entity.User;
import com.ecosmart.ecosmart_bin.repository.RewardRepository;
import com.ecosmart.ecosmart_bin.repository.RewardUserRepository;
import com.ecosmart.ecosmart_bin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final RewardUserRepository rewardUserRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public Reward creerRecompense(Reward reward) {
        reward.setDisponible(true);
        return rewardRepository.save(reward);
    }

    public List<Reward> getRecompensesDisponibles() {
        return rewardRepository.findByDisponibleTrue();
    }

    public List<Reward> getRecompensesAccessibles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return rewardRepository.findByPointsRequisLessThanEqual(user.getPoints());
    }

    public RewardUser echangerRecompense(Long userId, Long rewardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Récompense introuvable"));

        if (!reward.isDisponible()) {
            throw new RuntimeException("Récompense non disponible");
        }

        userService.deductPoints(user, reward.getPointsRequis());

        RewardUser rewardUser = RewardUser.builder()
                .user(user)
                .reward(reward)
                .dateEchange(LocalDateTime.now())
                .statut("EN_ATTENTE")
                .build();

        return rewardUserRepository.save(rewardUser);
    }

    public List<RewardUser> getHistoriqueEchanges(Long userId) {
        return rewardUserRepository.findByUserId(userId);
    }

    public List<Reward> toutes() {
        return rewardRepository.findAll();
    }
}