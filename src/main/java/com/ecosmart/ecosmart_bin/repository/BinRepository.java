// BinRepository.java
package com.ecosmart.ecosmart_bin.repository;

import com.ecosmart.ecosmart_bin.entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BinRepository extends JpaRepository<Bin, Long> {
    List<Bin> findByEtat(String etat);
}