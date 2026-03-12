// RewardRepository.java
package com.ecosmart.ecosmart_bin.repository;

import com.ecosmart.ecosmart_bin.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByDisponibleTrue();
    List<Reward> findByPointsRequisLessThanEqual(int points);
}