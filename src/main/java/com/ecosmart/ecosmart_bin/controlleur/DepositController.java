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
 *  DepositController — Gestion des dépôts de plastique
 * ============================================================
 *
 *  BASE URL : /api/depot
 *
 *  Endpoints :
 *  ┌────────────────────────────────────────────────────────────────┐
 *  │  POST  /api/depot                    → Enregistrer un dépôt    │
 *  │  GET   /api/depot/historique/{id}    → Historique utilisateur  │
 *  │  GET   /api/depot/total-poids        → Total plastique collecté│
 *  └────────────────────────────────────────────────────────────────┘
 *
 *  Logique de points : 1 point par tranche de 100g déposés.
 *  Exemple : 350g déposés → 3 points gagnés.
 *
 *  ⚠️  FIX : on retourne des DepositDTO et non des entités Deposit
 *  pour éviter la boucle infinie JSON :
 *  Deposit → User → List<Deposit> → User → ... = CRASH 500
 */
@RestController
@RequestMapping("/api/depot")
@RequiredArgsConstructor
@Tag(name = "Dépôt Plastique", description = "Enregistrement des dépôts et calcul des points")
public class DepositController {

    private final DepositService depositService;

    // =========================================================
    //  POST /api/depot
    // =========================================================

    /**
     * Enregistre un dépôt de plastique fait par un utilisateur sur une borne.
     *
     * Ce que fait cet endpoint :
     *  1. Vérifie que l'utilisateur et la borne existent
     *  2. Vérifie que la borne n'est pas PLEIN
     *  3. Calcule les points gagnés (1 pt / 100g)
     *  4. Sauvegarde le dépôt en base
     *  5. Met à jour le niveau de remplissage de la borne
     *  6. Ajoute les points au compte de l'utilisateur
     *
     * Corps attendu :
     * { "userId": 1, "binId": 2, "poids": 350.0 }
     *
     * Réponse 200 :
     * { "message", "depositId", "poids", "pointsGagnes", "date" }
     *
     * Réponse 400 : borne introuvable / utilisateur introuvable / borne pleine
     */
    @PostMapping
    @Operation(
            summary = "Enregistrer un dépôt de plastique",
            description = "Calcule automatiquement les points (1 pt / 100g) et met à jour " +
                    "le niveau de remplissage de la borne. Erreur si la borne est PLEIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dépôt enregistré, points attribués"),
            @ApiResponse(responseCode = "400", description = "Borne pleine, ou utilisateur/borne introuvable")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value =
                    "{ \"userId\": 1, \"binId\": 1, \"poids\": 350.0 }"))
    )
    public ResponseEntity<Map<String, Object>> depot(@RequestBody DepositRequest request) {
        try {
            Deposit deposit = depositService.enregistrerDepot(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Dépôt enregistré avec succès !");
            response.put("depositId", deposit.getId());
            response.put("poids", deposit.getPoids() + "g");
            response.put("pointsGagnes", deposit.getPointsGagnes());
            response.put("date", deposit.getDateDepot().toString());
            response.put("totalPointsUser", deposit.getUser().getPoints());
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

    /**
     * Retourne l'historique complet des dépôts d'un utilisateur.
     *
     * FIX : retourne List<DepositDTO> et non List<Deposit>
     * pour éviter la boucle infinie JSON.
     *
     * Réponse 200 : liste de DepositDTO avec userId, binId, poids, points, date
     */
    @GetMapping("/historique/{userId}")
    @Operation(
            summary = "Historique des dépôts d'un utilisateur",
            description = "Retourne tous les dépôts effectués par cet utilisateur, triés du plus récent. " +
                    "Retourne une liste vide [] si l'utilisateur n'a jamais déposé."
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
    //  GET /api/depot/total-poids
    // =========================================================

    /**
     * Retourne le total de plastique collecté sur toutes les bornes.
     * Utilisé dans le dashboard admin.
     *
     * Réponse 200 :
     * { "totalGrammes": 12500.0, "totalKg": 12.5 }
     */
    @GetMapping("/total-poids")
    @Operation(
            summary = "Total du plastique collecté",
            description = "Retourne la somme de tous les dépôts en grammes et en kg. " +
                    "Utilisé pour les statistiques du dashboard admin."
    )
    @ApiResponse(responseCode = "200", description = "Total en grammes et en kg")
    public ResponseEntity<Map<String, Object>> totalPoids() {
        double total = depositService.getTotalPoidsCollecte();
        Map<String, Object> response = new HashMap<>();
        response.put("totalGrammes", total);
        response.put("totalKg", total / 1000);
        return ResponseEntity.ok(response);
    }
}
