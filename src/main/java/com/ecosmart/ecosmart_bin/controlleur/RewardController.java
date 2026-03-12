package com.ecosmart.ecosmart_bin.controlleur;

import com.ecosmart.ecosmart_bin.dto.RewardUserDTO;
import com.ecosmart.ecosmart_bin.entity.Reward;
import com.ecosmart.ecosmart_bin.entity.RewardUser;
import com.ecosmart.ecosmart_bin.service.RewardService;
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
 *  RewardController — Gestion des récompenses
 * ============================================================
 *
 *  BASE URL : /api/rewards
 *
 *  Endpoints :
 *  ┌──────────────────────────────────────────────────────────────────┐
 *  │  POST  /api/rewards                   → Créer une récompense     │
 *  │  GET   /api/rewards                   → Récompenses disponibles  │
 *  │  GET   /api/rewards/accessibles/{id}  → Selon points user        │
 *  │  POST  /api/rewards/echanger          → Échanger des points      │
 *  │  GET   /api/rewards/historique/{id}   → Historique échanges      │
 *  └──────────────────────────────────────────────────────────────────┘
 *
 *  Flux d'un échange :
 *   1. L'utilisateur consulte les récompenses accessibles
 *   2. Il choisit une récompense et appelle /echanger
 *   3. Ses points sont déduits immédiatement
 *   4. L'échange passe en statut EN_ATTENTE
 *   5. L'admin valide → statut VALIDE
 *
 *  ⚠️  FIX : on retourne des RewardUserDTO et non des entités RewardUser
 *  pour éviter la boucle infinie JSON.
 */
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Tag(name = "Récompenses", description = "Gestion des récompenses et échanges de points")
public class RewardController {

    private final RewardService rewardService;

    // =========================================================
    //  POST /api/rewards
    // =========================================================

    /**
     * Crée une nouvelle récompense (réservé à l'Admin).
     * La récompense est automatiquement marquée comme disponible.
     *
     * Corps attendu :
     * { "nom": "Bon d'achat 5000 FCFA", "description": "...", "pointsRequis": 50 }
     *
     * Réponse 200 : la récompense créée avec son id
     */
    @PostMapping
    @Operation(
            summary = "Créer une récompense (Admin)",
            description = "Ajoute une nouvelle récompense au catalogue. " +
                    "Elle est automatiquement marquée disponible=true."
    )
    @ApiResponse(responseCode = "200", description = "Récompense créée")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value =
                    "{ \"nom\": \"Bon d'achat 5000 FCFA\", " +
                            "\"description\": \"Valable dans les supermarchés partenaires\", " +
                            "\"pointsRequis\": 50 }"))
    )
    public ResponseEntity<Reward> creer(@RequestBody Reward reward) {
        return ResponseEntity.ok(rewardService.creerRecompense(reward));
    }

    // =========================================================
    //  GET /api/rewards
    // =========================================================

    /**
     * Retourne toutes les récompenses dont disponible=true.
     * Affiché dans le catalogue de récompenses de l'application.
     *
     * Réponse 200 : liste des récompenses disponibles
     */
    @GetMapping
    @Operation(
            summary = "Catalogue des récompenses disponibles",
            description = "Retourne toutes les récompenses actives (disponible=true). " +
                    "Affiché dans l'onglet Récompenses de l'application."
    )
    @ApiResponse(responseCode = "200", description = "Liste des récompenses")
    public ResponseEntity<List<Reward>> disponibles() {
        return ResponseEntity.ok(rewardService.getRecompensesDisponibles());
    }

    // =========================================================
    //  GET /api/rewards/accessibles/{userId}
    // =========================================================

    /**
     * Retourne uniquement les récompenses que l'utilisateur peut
     * se payer avec ses points actuels.
     *
     * Exemple : si l'utilisateur a 35 points, retourne les récompenses
     * dont pointsRequis <= 35.
     *
     * Réponse 200 : liste filtrée selon les points de l'utilisateur
     * Réponse 500 : utilisateur introuvable
     */
    @GetMapping("/accessibles/{userId}")
    @Operation(
            summary = "Récompenses accessibles selon les points de l'utilisateur",
            description = "Filtre les récompenses en fonction des points disponibles de l'utilisateur. " +
                    "Permet d'afficher uniquement ce que l'utilisateur peut se permettre."
    )
    @ApiResponse(responseCode = "200", description = "Récompenses accessibles")
    public ResponseEntity<List<Reward>> accessibles(
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(rewardService.getRecompensesAccessibles(userId));
    }

    // =========================================================
    //  POST /api/rewards/echanger?userId=1&rewardId=2
    // =========================================================

    /**
     * Échange les points d'un utilisateur contre une récompense.
     *
     * Ce que fait cet endpoint :
     *  1. Vérifie que l'utilisateur existe
     *  2. Vérifie que la récompense existe et est disponible
     *  3. Vérifie que l'utilisateur a assez de points
     *  4. Déduit les points du compte utilisateur
     *  5. Crée un RewardUser avec statut EN_ATTENTE
     *
     * Exemple : POST /api/rewards/echanger?userId=1&rewardId=2
     *
     * Réponse 200 : { "message", "statut": "EN_ATTENTE", "date" }
     * Réponse 400 : points insuffisants / récompense non disponible
     */
    @PostMapping("/echanger")
    @Operation(
            summary = "Échanger des points contre une récompense",
            description = "Déduit les points du compte et crée un échange en statut EN_ATTENTE. " +
                    "Erreur 400 si l'utilisateur n'a pas assez de points."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Échange effectué, statut EN_ATTENTE"),
            @ApiResponse(responseCode = "400", description = "Points insuffisants ou récompense indisponible")
    })
    public ResponseEntity<Map<String, Object>> echanger(
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @RequestParam Long userId,
            @Parameter(description = "ID de la récompense", example = "2")
            @RequestParam Long rewardId) {
        try {
            RewardUser rewardUser = rewardService.echangerRecompense(userId, rewardId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Échange effectué avec succès !");
            response.put("recompense", rewardUser.getReward().getNom());
            response.put("pointsDepenses", rewardUser.getReward().getPointsRequis());
            response.put("statut", rewardUser.getStatut());
            response.put("date", rewardUser.getDateEchange().toString());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> erreur = new HashMap<>();
            erreur.put("erreur", e.getMessage());
            return ResponseEntity.badRequest().body(erreur);
        }
    }

    // =========================================================
    //  GET /api/rewards/historique/{userId}
    // =========================================================

    /**
     * Retourne l'historique de tous les échanges d'un utilisateur.
     *
     * FIX : retourne List<RewardUserDTO> et non List<RewardUser>
     * pour éviter la boucle infinie JSON.
     *
     * Réponse 200 : liste des échanges avec statut, date, récompense
     */
    @GetMapping("/historique/{userId}")
    @Operation(
            summary = "Historique des échanges d'un utilisateur",
            description = "Retourne tous les échanges effectués par cet utilisateur " +
                    "avec leur statut (EN_ATTENTE, VALIDE, REFUSE)."
    )
    @ApiResponse(responseCode = "200", description = "Historique des échanges")
    public ResponseEntity<List<RewardUserDTO>> historique(
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable Long userId) {
        List<RewardUserDTO> dtos = rewardService.getHistoriqueEchanges(userId)
                .stream()
                .map(RewardUserDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
