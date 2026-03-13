package com.ecosmart.ecosmart_bin.dto;

import com.ecosmart.ecosmart_bin.entity.TypePlastique;
import lombok.Data;

/**
 * Corps de la requête POST /api/depot
 *
 * Envoyé par le dispositif IoT après le scan de la plaque niveau 1.
 *
 * Deux cas possibles :
 *
 * CAS 1 — plastique reconnu :
 * {
 *   "userId": 1,
 *   "binId": 2,
 *   "typePlastique": "PET",
 *   "scanResultat": "ACCEPTE"
 * }
 *
 * CAS 2 — déchet NON reconnu (verre, métal, carton...) :
 * {
 *   "userId": 1,
 *   "binId": 2,
 *   "typePlastique": null,
 *   "scanResultat": "REFUSE"
 * }
 *
 * Dans les deux cas : la plaque s'ouvre TOUJOURS.
 * Seul CAS 1 attribue des points.
 */
@Data
public class DepositRequest {
    private Long userId;
    private Long binId;
    private TypePlastique typePlastique; // null si REFUSE
    private String scanResultat;         // ACCEPTE ou REFUSE
}
