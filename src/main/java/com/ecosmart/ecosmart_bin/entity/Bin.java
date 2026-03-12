package com.ecosmart.ecosmart_bin.entity;

import jakarta.persistence.GenerationType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String localisation;
    private String etat; // vide / partiellement / plein
    private double niveauRemplissage;
}
