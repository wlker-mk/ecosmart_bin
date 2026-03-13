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

    /**
     * Crée une nouvelle borne.
     * Valeurs initiales :
     *   - etat = VIDE
     *   - inclinaisonActuelle = 0.0
     *   - nombreDepots = 0
     * L'admin doit configurer inclinaisonSeuil via PUT /api/bins/{id}/seuil
     */
    public Bin creerBorne(Bin bin) {
        bin.setEtat("VIDE");
        bin.setInclinaisonActuelle(0.0);
        bin.setNombreDepots(0);
        // inclinaisonSeuil doit être fourni dans le corps de la requête
        // Si non fourni, on met une valeur par défaut de 30 degrés
        if (bin.getInclinaisonSeuil() == 0.0) {
            bin.setInclinaisonSeuil(30.0);
        }
        return binRepository.save(bin);
    }

    public List<Bin> toutes() {
        return binRepository.findAll();
    }

    public Bin getById(Long id) {
        return binRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borne introuvable : " + id));
    }

    /**
     * Mise à jour de l'inclinaison par le capteur IoT niveau 2.
     *
     * Calcul de l'état selon le ratio inclinaisonActuelle / inclinaisonSeuil :
     *   - >= seuil       -> PLEIN   (bloque les dépôts)
     *   - >= 70% seuil   -> PARTIEL
     *   - < 70% seuil    -> VIDE
     */
    public Bin mettreAJourInclinaison(Long id, double inclinaisonActuelle) {
        Bin bin = getById(id);
        bin.setInclinaisonActuelle(inclinaisonActuelle);

        double seuil = bin.getInclinaisonSeuil();
        if (inclinaisonActuelle >= seuil) {
            bin.setEtat("PLEIN");
        } else if (inclinaisonActuelle >= seuil * 0.7) {
            bin.setEtat("PARTIEL");
        } else {
            bin.setEtat("VIDE");
        }

        return binRepository.save(bin);
    }

    /**
     * Remet la borne à zéro après vidage par l'agent de collecte.
     */
    public Bin remettreBorneAZero(Long id) {
        Bin bin = getById(id);
        bin.setEtat("VIDE");
        bin.setInclinaisonActuelle(0.0);
        bin.setNombreDepots(0);
        return binRepository.save(bin);
    }

    public List<Bin> getBornesPlein() {
        return binRepository.findByEtat("PLEIN");
    }

    public void supprimerBorne(Long id) {
        binRepository.deleteById(id);
    }
}
