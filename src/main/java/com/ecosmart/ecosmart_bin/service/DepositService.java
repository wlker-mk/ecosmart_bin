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

    /**
     * Enregistre un scan de la borne (niveau 1 — plaque).
     *
     * Règles :
     *  - La plaque s'ouvre TOUJOURS (niveau 2 reçoit le déchet dans tous les cas)
     *  - Points attribués SEULEMENT si scanResultat = ACCEPTE
     *  - Si étudiant -> points x2 (géré dans UserService.addPoints)
     *  - On incrémente nombreDepots dans tous les cas
     */
    public Deposit enregistrerDepot(DepositRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Bin bin = binRepository.findById(request.getBinId())
                .orElseThrow(() -> new RuntimeException("Borne introuvable"));

        if ("PLEIN".equals(bin.getEtat())) {
            throw new RuntimeException("La borne est pleine, veuillez choisir une autre borne");
        }

        // Points = 0 par défaut (REFUSE ou typePlastique null)
        int pointsGagnes = 0;
        if ("ACCEPTE".equals(request.getScanResultat()) && request.getTypePlastique() != null) {
            pointsGagnes = request.getTypePlastique().getPoints();
        }

        Deposit deposit = Deposit.builder()
                .user(user)
                .bin(bin)
                .typePlastique(request.getTypePlastique())
                .scanResultat(request.getScanResultat())
                .pointsGagnes(pointsGagnes)
                .dateDepot(LocalDateTime.now())
                .build();

        depositRepository.save(deposit);

        // Incrémenter le compteur de dépôts de la borne (plaque ouvre toujours)
        bin.setNombreDepots(bin.getNombreDepots() + 1);
        binRepository.save(bin);

        // Ajouter les points seulement si plastique reconnu
        if (pointsGagnes > 0) {
            userService.addPoints(user, pointsGagnes); // x2 si étudiant
        }

        return deposit;
    }

    public List<Deposit> getHistoriqueByUser(Long userId) {
        return depositRepository.findByUserId(userId);
    }

    public List<Deposit> getAllDeposits() {
        return depositRepository.findAll();
    }

    public Long getTotalDepotsAcceptes() {
        return depositRepository.countByScanResultat("ACCEPTE");
    }

    public Long getTotalDepotsRefuses() {
        return depositRepository.countByScanResultat("REFUSE");
    }
}
