package com.ecosmart.ecosmart_bin.dto;

import lombok.Data;

// Nouveau fichier — ce que le capteur niveau 2 envoie au backend
@Data
public class BinUpdateRequest {
    private double inclinaisonActuelle; // angle lu par le capteur
}
