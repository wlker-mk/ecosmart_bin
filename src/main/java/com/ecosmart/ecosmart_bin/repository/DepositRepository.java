package com.ecosmart.ecosmart_bin.repository;

import com.ecosmart.ecosmart_bin.entity.Deposit;
import com.ecosmart.ecosmart_bin.entity.TypePlastique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

    List<Deposit> findByUserId(Long userId);

    // Compter les dépôts acceptés / refusés (pour les stats admin)
    Long countByScanResultat(String scanResultat);

    // Compter par type de plastique (pour les stats)
    Long countByTypePlastique(TypePlastique typePlastique);

    // Total des points distribués
    @Query("SELECT SUM(d.pointsGagnes) FROM Deposit d WHERE d.scanResultat = 'ACCEPTE'")
    Long totalPointsDistribues();

    // Total points par utilisateur
    @Query("SELECT SUM(d.pointsGagnes) FROM Deposit d WHERE d.user.id = :userId")
    Integer totalPointsByUser(Long userId);

    // Tous les dépôts acceptés (plastique reconnu)
    List<Deposit> findByScanResultat(String scanResultat);
}
