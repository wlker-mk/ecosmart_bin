// DepositService.java
package com.ecosmart.ecosmart_bin.service;

import com.ecosmart.ecosmart_bin.dto.DepositRequest;
import com.ecosmart.ecosmart_bin.entity.Bin;
import com.ecosmart.ecosmart_bin.entity.Deposit;
import com.ecosmart.ecosmart_bin.entity.User;
import com.ecosmart.ecosmart_bin.repository.BinRepository;
import com.ecosmart.ecosmart_bin.repository.DepositRepository;
import com.ecosmart.ecosmart_bin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final BinRepository binRepository;
    private final UserService userService;

    private static final double POINTS_PAR_100G = 1.0;

    public Deposit enregistrerDepot(DepositRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Bin bin = binRepository.findById(request.getBinId())
                .orElseThrow(() -> new RuntimeException("Borne introuvable"));

        if ("PLEIN".equals(bin.getEtat())) {
            throw new RuntimeException("La borne est pleine");
        }

        int pointsGagnes = (int) (request.getPoids() / 100 * POINTS_PAR_100G);

        Deposit deposit = Deposit.builder()
                .user(user)
                .bin(bin)
                .poids(request.getPoids())
                .pointsGagnes(pointsGagnes)
                .dateDepot(LocalDateTime.now())
                .build();

        depositRepository.save(deposit);

        double nouveauNiveau = bin.getNiveauRemplissage() + (request.getPoids() / 10000.0 * 100);
        bin.setNiveauRemplissage(Math.min(nouveauNiveau, 100));
        bin.setEtat(nouveauNiveau >= 90 ? "PLEIN" : nouveauNiveau >= 50 ? "PARTIEL" : "VIDE");
        binRepository.save(bin);

        userService.addPoints(user, pointsGagnes);
        return deposit;
    }

    public List<Deposit> getHistoriqueByUser(Long userId) {
        return depositRepository.findByUserId(userId);
    }

    public List<Deposit> getAllDeposits() {
        return depositRepository.findAll();
    }

    public Double getTotalPoidsCollecte() {
        Double total = depositRepository.totalPoidsCollecte();
        return total != null ? total : 0.0;
    }
}