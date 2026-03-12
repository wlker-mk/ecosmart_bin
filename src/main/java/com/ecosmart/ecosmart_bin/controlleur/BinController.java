package com.ecosmart.ecosmart_bin.controlleur;

import com.ecosmart.ecosmart_bin.entity.Bin;
import com.ecosmart.ecosmart_bin.service.BinService;
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

/**
 * ============================================================
 *  BinController — Gestion des bornes intelligentes
 * ============================================================
 *
 *  BASE URL : /api/bins
 *
 *  Endpoints :
 *  ┌─────────────────────────────────────────────────────────────────┐
 *  │  POST   /api/bins              → Créer une borne (Admin)        │
 *  │  GET    /api/bins              → Lister toutes les bornes        │
 *  │  GET    /api/bins/{id}         → Détail d'une borne             │
 *  │  PUT    /api/bins/{id}/niveau  → Mettre à jour niveau (IoT)     │
 *  │  GET    /api/bins/pleines      → Bornes à vider (Agent)         │
 *  │  DELETE /api/bins/{id}         → Supprimer une borne (Admin)    │
 *  └─────────────────────────────────────────────────────────────────┘
 *
 *  États d'une borne :
 *   - VIDE     : niveau < 50%
 *   - PARTIEL  : 50% ≤ niveau < 90%
 *   - PLEIN    : niveau ≥ 90%  → bloque les nouveaux dépôts
 *
 *  Note : l'endpoint PUT /niveau est appelé par la borne IoT
 *  pour mettre à jour son niveau en temps réel.
 */
@RestController
@RequestMapping("/api/bins")
@RequiredArgsConstructor
@Tag(name = "Bornes", description = "Gestion et suivi des bornes intelligentes de collecte")
public class BinController {

    private final BinService binService;

    // =========================================================
    //  POST /api/bins
    // =========================================================

    /**
     * Crée et enregistre une nouvelle borne dans le système.
     * La borne démarre avec l'état VIDE et un niveau de 0%.
     *
     * Corps attendu :
     * { "nom": "Borne Campus", "localisation": "Université de Lomé",
     *   "latitude": 6.1722, "longitude": 1.2314 }
     *
     * Réponse 200 : l'entité Bin créée avec son id, etat=VIDE, niveauRemplissage=0
     */
    @PostMapping
    @Operation(
            summary = "Créer une nouvelle borne (Admin)",
            description = "Enregistre une borne avec état VIDE et niveau 0%. " +
                    "Fournir latitude/longitude pour la carte des bornes."
    )
    @ApiResponse(responseCode = "200", description = "Borne créée avec succès")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value =
                    "{ \"nom\": \"Borne Campus\", \"localisation\": \"Université de Lomé\", " +
                            "\"latitude\": 6.1722, \"longitude\": 1.2314 }"))
    )
    public ResponseEntity<Bin> creer(@RequestBody Bin bin) {
        return ResponseEntity.ok(binService.creerBorne(bin));
    }

    // =========================================================
    //  GET /api/bins
    // =========================================================

    /**
     * Retourne la liste de toutes les bornes enregistrées.
     * Utilisé pour afficher la carte des bornes dans l'application.
     *
     * Réponse 200 : liste de toutes les bornes avec leur état actuel
     */
    @GetMapping
    @Operation(
            summary = "Lister toutes les bornes",
            description = "Retourne toutes les bornes avec leur localisation, état et niveau de remplissage. " +
                    "Utilisé pour la carte interactive de l'application."
    )
    @ApiResponse(responseCode = "200", description = "Liste des bornes")
    public ResponseEntity<List<Bin>> toutes() {
        return ResponseEntity.ok(binService.toutes());
    }

    // =========================================================
    //  GET /api/bins/{id}
    // =========================================================

    /**
     * Retourne le détail complet d'une borne spécifique.
     *
     * Réponse 200 : la borne avec son état, niveau, localisation GPS
     * Réponse 500 : borne introuvable (RuntimeException)
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Détail d'une borne",
            description = "Retourne toutes les informations d'une borne : état, niveau, localisation GPS."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borne trouvée"),
            @ApiResponse(responseCode = "500", description = "Borne introuvable")
    })
    public ResponseEntity<Bin> getById(
            @Parameter(description = "ID de la borne", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(binService.getById(id));
    }

    // =========================================================
    //  PUT /api/bins/{id}/niveau?niveau=75.5
    // =========================================================

    /**
     * Met à jour le niveau de remplissage d'une borne.
     * Cet endpoint est appelé par la borne IoT automatiquement.
     *
     * Calcul automatique de l'état :
     *  - niveau >= 90 → PLEIN  (bloque les dépôts)
     *  - niveau >= 50 → PARTIEL
     *  - niveau < 50  → VIDE
     *
     * Exemple : PUT /api/bins/1/niveau?niveau=75.5
     *
     * Réponse 200 : la borne mise à jour avec le nouvel état
     */
    @PutMapping("/{id}/niveau")
    @Operation(
            summary = "Mettre à jour le niveau de remplissage (IoT)",
            description = "Appelé automatiquement par la borne IoT. " +
                    "Met à jour le niveau (0-100%) et recalcule l'état : " +
                    "VIDE (<50%), PARTIEL (50-89%), PLEIN (≥90%)."
    )
    @ApiResponse(responseCode = "200", description = "Niveau mis à jour")
    public ResponseEntity<Bin> updateNiveau(
            @Parameter(description = "ID de la borne", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouveau niveau en % (0 à 100)", example = "75.5")
            @RequestParam double niveau) {
        return ResponseEntity.ok(binService.mettreAJourNiveau(id, niveau));
    }

    // =========================================================
    //  GET /api/bins/pleines
    // =========================================================

    /**
     * Retourne uniquement les bornes dont l'état est PLEIN.
     * Utilisé par les agents de collecte pour savoir quelles
     * bornes doivent être vidées en priorité.
     *
     * Réponse 200 : liste des bornes PLEIN (peut être vide [])
     */
    @GetMapping("/pleines")
    @Operation(
            summary = "Lister les bornes pleines à vider",
            description = "Retourne uniquement les bornes en état PLEIN (niveau ≥ 90%). " +
                    "Utilisé par les agents de collecte pour planifier leurs tournées."
    )
    @ApiResponse(responseCode = "200", description = "Liste des bornes pleines")
    public ResponseEntity<List<Bin>> pleines() {
        return ResponseEntity.ok(binService.getBornesPlein());
    }

    // =========================================================
    //  DELETE /api/bins/{id}
    // =========================================================

    /**
     * Supprime définitivement une borne du système.
     * ⚠️ Action irréversible — réservée à l'administrateur.
     *
     * Réponse 200 : { "message": "Borne supprimée" }
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une borne (Admin)",
            description = "Suppression définitive. ⚠️ Action irréversible. " +
                    "Les dépôts liés à cette borne restent en base."
    )
    @ApiResponse(responseCode = "200", description = "Borne supprimée")
    public ResponseEntity<Map<String, Object>> supprimer(
            @Parameter(description = "ID de la borne à supprimer", example = "1")
            @PathVariable Long id) {
        binService.supprimerBorne(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Borne supprimée avec succès");
        return ResponseEntity.ok(response);
    }
}
