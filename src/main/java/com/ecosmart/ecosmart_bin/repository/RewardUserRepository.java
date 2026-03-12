// RewardUserRepository.java
package com.ecosmart.ecosmart_bin.repository;

import com.ecosmart.ecosmart_bin.entity.RewardUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RewardUserRepository extends JpaRepository<RewardUser, Long> {
    List<RewardUser> findByUserId(Long userId);
}