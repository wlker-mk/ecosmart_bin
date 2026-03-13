package com.ecosmart.ecosmart_bin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String localisation;
    private String etat; // vide / partiellement / plein
    // APRÈS — données réelles du capteur physique
    private double inclinaisonActuelle;  // angle envoyé par le capteur (degrés)
    private double inclinaisonSeuil;     // angle configuré = "bac plein" (ex: 30°)
    private int nombreDepots;            // compteur de dépôts dans ce bac

    private double latitude;
    private double longitude;
}
