package com.ecosmart.ecosmart_bin.controlleur;

import com.ecosmart.ecosmart_bin.dto.DepositDTO;
import com.ecosmart.ecosmart_bin.dto.DepositRequest;
import com.ecosmart.ecosmart_bin.entity.Deposit;
import com.ecosmart.ecosmart_bin.service.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  DepositController — Scan et dépôt de déchets
 * ============================================================
 *
 *  BASE URL : /api/depot
 *
 *  ┌────────────────────────────────────────────────────────────────┐
 *  │  POST  /api/depot                  -> Enregistrer un scan      │
 *  │  GET   /api/depot/historique/{id}  -> Historique utilisateur   │
 *  │  GET   /api/depot/stats            -> Stats globales scan      │
 *  └────────────────────────────────────────────────────────────────┘
 *
 *  Règle fondamentale :
 *   - La plaque (niveau 1) s'ouvre TOUJOURS après le scan
 *   - Les points sont attribués SEULEMENT si scanResultat = ACCEPTE
 *   - Si étudiant -> points x2 (géré dans UserService)
 *
 *  Points par type de plastique :
 *   PET=5  HDPE=4  PVC=3  PP=3  LDPE=2  PS=2  AUTRE=1
 */
@RestController
@RequestMapping("/api/depot")
@RequiredArgsConstructor
@Tag(name = "Depot Plastique", description = "Scan IoT et enregistrement des dépôts")
public class DepositController {

    private final DepositService depositService;

    // =========================================================
    //  POST /api/depot
    // =========================================================
    @PostMapping
    @Operation(
            summary = "Enregistrer un scan (IoT niveau 1)",
            description = "Appelé par le dispositif IoT après le scan de la plaque. " +
                    "La plaque s'ouvre TOUJOURS. " +
                    "Points attribués SEULEMENT si scanResultat=ACCEPTE et typePlastique non null. " +
                    "Si scanResultat=REFUSE : 0 points mais la plaque ouvre quand même."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Scan enregistré, plaque ouverte"),
            @ApiResponse(responseCode = "400", description = "Borne pleine ou user/borne introuvable")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "Plastique reconnu (PET)", value =
                    "{ \"userId\": 1, \"binId\": 1, \"typePlastique\": \"PET\", \"scanResultat\": \"ACCEPTE\" }"),
            @ExampleObject(name = "Non reconnu (verre, métal...)", value =
                    "{ \"userId\": 1, \"binId\": 1, \"typePlastique\": null, \"scanResultat\": \"REFUSE\" }")
    }))
    public ResponseEntity<Map<String, Object>> depot(@RequestBody DepositRequest request) {
        try {
            Deposit deposit = depositService.enregistrerDepot(request);
            Map<String, Object> response = new HashMap<>();

            // Message différent selon le résultat du scan
            if ("ACCEPTE".equals(deposit.getScanResultat())) {
                response.put("message", "Plastique reconnu ! Points attribués.");
                response.put("typePlastique", deposit.getTypePlastique());
                response.put("pointsGagnes", deposit.getPointsGagnes());
                response.put("totalPointsUser", deposit.getUser().getPoints());
            } else {
                response.put("message", "Déchet non reconnu comme plastique. Aucun point attribué.");
                response.put("pointsGagnes", 0);
            }

            response.put("depositId", deposit.getId());
            response.put("scanResultat", deposit.getScanResultat());
            response.put("plaqueOuverte", true); // toujours true
            response.put("date", deposit.getDateDepot().toString());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> erreur = new HashMap<>();
            erreur.put("erreur", e.getMessage());
            return ResponseEntity.badRequest().body(erreur);
        }
    }

    // =========================================================
    //  GET /api/depot/historique/{userId}
    // =========================================================
    @GetMapping("/historique/{userId}")
    @Operation(
            summary = "Historique des dépôts d'un utilisateur",
            description = "Retourne tous les scans (acceptés ET refusés) de l'utilisateur."
    )
    @ApiResponse(responseCode = "200", description = "Liste des dépôts")
    public ResponseEntity<List<DepositDTO>> historique(
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable Long userId) {
        List<DepositDTO> dtos = depositService.getHistoriqueByUser(userId)
                .stream()
                .map(DepositDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // =========================================================
    //  GET /api/depot/stats
    // =========================================================
    @GetMapping("/stats")
    @Operation(
            summary = "Statistiques globales des scans",
            description = "Retourne le nombre total de scans acceptés et refusés. Utilisé pour le dashboard admin."
    )
    @ApiResponse(responseCode = "200", description = "Statistiques des scans")
    public ResponseEntity<Map<String, Object>> stats() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalAcceptes", depositService.getTotalDepotsAcceptes());
        response.put("totalRefuses", depositService.getTotalDepotsRefuses());
        response.put("totalPointsDistribues", depositService.getTotalDepotsAcceptes());
        return ResponseEntity.ok(response);
    }
}
