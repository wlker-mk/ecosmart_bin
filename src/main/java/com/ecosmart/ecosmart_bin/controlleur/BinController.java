package com.ecosmart.ecosmart_bin.controlleur;

import com.ecosmart.ecosmart_bin.dto.BinUpdateRequest;
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
 *  ┌──────────────────────────────────────────────────────────────────────┐
 *  │  POST   /api/bins                   -> Créer une borne (Admin)       │
 *  │  GET    /api/bins                   -> Lister toutes les bornes       │
 *  │  GET    /api/bins/{id}              -> Détail d'une borne            │
 *  │  PUT    /api/bins/{id}/inclinaison  -> Capteur niveau 2 (IoT)       │
 *  │  PUT    /api/bins/{id}/reset        -> Remettre à zéro après vidage  │
 *  │  GET    /api/bins/pleines           -> Bornes à vider (Agent)        │
 *  │  DELETE /api/bins/{id}             -> Supprimer une borne (Admin)   │
 *  └──────────────────────────────────────────────────────────────────────┘
 *
 *  Détection bac plein :
 *   - Le capteur d'inclinaison (niveau 2) envoie l'angle actuel
 *   - Si angle >= inclinaisonSeuil configuré -> etat = PLEIN
 *   - Si angle >= 70% du seuil              -> etat = PARTIEL
 *   - Sinon                                 -> etat = VIDE
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
    @PostMapping
    @Operation(
            summary = "Créer une nouvelle borne (Admin)",
            description = "Créer une borne. inclinaisonSeuil = angle en degrés au-delà duquel le bac est PLEIN. " +
                    "Par défaut 30.0° si non fourni."
    )
    @ApiResponse(responseCode = "200", description = "Borne créée")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value =
                    "{ \"nom\": \"Borne Campus\", \"localisation\": \"Université de Lomé\", " +
                            "\"inclinaisonSeuil\": 30.0, \"latitude\": 6.1722, \"longitude\": 1.2314 }"))
    )
    public ResponseEntity<Bin> creer(@RequestBody Bin bin) {
        return ResponseEntity.ok(binService.creerBorne(bin));
    }

    // =========================================================
    //  GET /api/bins
    // =========================================================
    @GetMapping
    @Operation(summary = "Lister toutes les bornes", description = "Retourne toutes les bornes avec leur état et inclinaison actuelle.")
    @ApiResponse(responseCode = "200", description = "Liste des bornes")
    public ResponseEntity<List<Bin>> toutes() {
        return ResponseEntity.ok(binService.toutes());
    }

    // =========================================================
    //  GET /api/bins/{id}
    // =========================================================
    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une borne")
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
    //  PUT /api/bins/{id}/inclinaison
    // =========================================================
    @PutMapping("/{id}/inclinaison")
    @Operation(
            summary = "Mise à jour inclinaison par le capteur IoT (niveau 2)",
            description = "Appelé automatiquement par le capteur d'inclinaison du bac. " +
                    "Si inclinaisonActuelle >= inclinaisonSeuil -> etat PLEIN, dépôts bloqués. " +
                    "Si >= 70% du seuil -> PARTIEL. Sinon -> VIDE."
    )
    @ApiResponse(responseCode = "200", description = "Borne mise à jour avec nouvel état")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = "{ \"inclinaisonActuelle\": 28.5 }"))
    )
    public ResponseEntity<Map<String, Object>> updateInclinaison(
            @Parameter(description = "ID de la borne", example = "1")
            @PathVariable Long id,
            @RequestBody BinUpdateRequest request) {

        Bin bin = binService.mettreAJourInclinaison(id, request.getInclinaisonActuelle());

        Map<String, Object> response = new HashMap<>();
        response.put("binId", bin.getId());
        response.put("inclinaisonActuelle", bin.getInclinaisonActuelle());
        response.put("inclinaisonSeuil", bin.getInclinaisonSeuil());
        response.put("etat", bin.getEtat());
        response.put("alertePlein", "PLEIN".equals(bin.getEtat()));

        return ResponseEntity.ok(response);
    }

    // =========================================================
    //  PUT /api/bins/{id}/reset
    // =========================================================
    @PutMapping("/{id}/reset")
    @Operation(
            summary = "Remettre la borne à zéro (Agent)",
            description = "Appelé par l'agent après avoir vidé le bac physiquement. " +
                    "Remet inclinaisonActuelle=0, etat=VIDE, nombreDepots=0."
    )
    @ApiResponse(responseCode = "200", description = "Borne remise à zéro")
    public ResponseEntity<Map<String, Object>> reset(
            @Parameter(description = "ID de la borne", example = "1")
            @PathVariable Long id) {
        Bin bin = binService.remettreBorneAZero(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Borne remise à zéro avec succès");
        response.put("binId", bin.getId());
        response.put("etat", bin.getEtat());
        return ResponseEntity.ok(response);
    }

    // =========================================================
    //  GET /api/bins/pleines
    // =========================================================
    @GetMapping("/pleines")
    @Operation(
            summary = "Bornes pleines à vider (Agent)",
            description = "Retourne uniquement les bornes en état PLEIN. Liste vide [] si aucune."
    )
    @ApiResponse(responseCode = "200", description = "Liste des bornes pleines")
    public ResponseEntity<List<Bin>> pleines() {
        return ResponseEntity.ok(binService.getBornesPlein());
    }

    // =========================================================
    //  DELETE /api/bins/{id}
    // =========================================================
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une borne (Admin)", description = "Action irréversible.")
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
