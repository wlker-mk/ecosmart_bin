// BinService.java
package com.ecosmart.ecosmart_bin.service;

import com.ecosmart.ecosmart_bin.entity.Bin;
import com.ecosmart.ecosmart_bin.repository.BinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BinService {

    private final BinRepository binRepository;

    public Bin creerBorne(Bin bin) {
        bin.setEtat("VIDE");
        bin.setNiveauRemplissage(0);
        return binRepository.save(bin);
    }

    public List<Bin> toutes() {
        return binRepository.findAll();
    }

    public Bin getById(Long id) {
        return binRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borne introuvable : " + id));
    }

    public Bin mettreAJourNiveau(Long id, double niveau) {
        Bin bin = getById(id);
        bin.setNiveauRemplissage(niveau);
        bin.setEtat(niveau >= 90 ? "PLEIN" : niveau >= 50 ? "PARTIEL" : "VIDE");
        return binRepository.save(bin);
    }

    public List<Bin> getBornesPlein() {
        return binRepository.findByEtat("PLEIN");
    }

    public void supprimerBorne(Long id) {
        binRepository.deleteById(id);
    }
}