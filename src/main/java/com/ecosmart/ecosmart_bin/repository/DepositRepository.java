// DepositRepository.java
package com.ecosmart.ecosmart_bin.repository;

import com.ecosmart.ecosmart_bin.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByUserId(Long userId);

    @Query("SELECT SUM(d.poids) FROM Deposit d")
    Double totalPoidsCollecte();

    @Query("SELECT SUM(d.pointsGagnes) FROM Deposit d WHERE d.user.id = :userId")
    Integer totalPointsByUser(Long userId);
}