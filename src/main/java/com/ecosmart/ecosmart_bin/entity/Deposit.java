// Deposit.java
package com.ecosmart.ecosmart_bin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double poids;
    private int pointsGagnes;
    private LocalDateTime dateDepot;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;
    // AJOUTER ces deux champs
    private TypePlastique typePlastique;  // type reconnu par le scan IA

    private String scanResultat; // ACCEPTE ou REFUSE
}