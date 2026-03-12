package com.ecosmart.ecosmart_bin.controlleur;

import com.ecosmart.ecosmart_bin.dto.DepositDTO;
import com.ecosmart.ecosmart_bin.dto.UserDTO;
import com.ecosmart.ecosmart_bin.service.BinService;
import com.ecosmart.ecosmart_bin.service.DepositService;
import com.ecosmart.ecosmart_bin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
 *  AdminController — Dashboard et statistiques
 * ============================================================
 *
 *  BASE URL : /api/admin
 *
 *  Endpoints :
 *  ┌──────────────────────────────────────────────────────────────┐
 *  │  GET  /api/admin/stats         → Statistiques globales       │
 *  │  GET  /api/admin/users         → Tous les utilisateurs       │
 *  │  GET  /api/admin/users/{id}    → Détail d'un utilisateur     │
 *  │  GET  /api/admin/deposits      → Tous les dépôts             │
 *  └──────────────────────────────────────────────────────────────┘
 *
 *  ⚠️  En production, sécuriser ces endpoints avec un rôle ADMIN :
 *  @PreAuthorize("hasRole('ADMIN')")
 *
 *  ⚠️  FIX : on retourne des DTOs pour éviter la boucle infinie JSON
 *  (User → List<Deposit> → User → ...).
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "🛠️ Administration", description = "Dashboard admin, statistiques et gestion des utilisateurs")
public class AdminController {

    private final UserService userService;
    private final DepositService depositService;
    private final BinService binService;

    // =========================================================
    //  GET /api/admin/stats
    // =========================================================

    /**
     * Retourne les statistiques globales du système ECOSMART-BIN.
     * Affiché sur le dashboard principal de l'administrateur.
     *
     * Réponse 200 :
     * {
     *   "totalUtilisateurs": 120,
     *   "totalDepots": 450,
     *   "totalPlastiqueGrammes": 125000.0,
     *   "totalPlastiqueKg": 125.0,
     *   "totalBornes": 8,
     *   "bornesPleines": 2
     * }
     */
    @GetMapping("/stats")
    @Operation(
            summary = "Statistiques globales du système",
            description = "Retourne le tableau de bord complet : nombre d'utilisateurs, " +
                    "dépôts totaux, plastique collecté en grammes et kg, " +
                    "nombre de bornes et bornes actuellement pleines."
    )
    @ApiResponse(responseCode = "200", description = "Statistiques du dashboard")
    public ResponseEntity<Map<String, Object>> stats() {
        double totalPoids = depositService.getTotalPoidsCollecte();
        Map<String, Object> response = new HashMap<>();
        response.put("totalUtilisateurs", userService.getAll().size());
        response.put("totalDepots", depositService.getAllDeposits().size());
        response.put("totalPlastiqueGrammes", totalPoids);
        response.put("totalPlastiqueKg", totalPoids / 1000);
        response.put("totalBornes", binService.toutes().size());
        response.put("bornesPleines", binService.getBornesPlein().size());
        return ResponseEntity.ok(response);
    }

    // =========================================================
    //  GET /api/admin/users
    // =========================================================

    /**
     * Retourne la liste de tous les utilisateurs inscrits.
     * Les mots de passe ne sont PAS exposés (on retourne des UserDTO).
     *
     * Réponse 200 : liste de UserDTO (sans le champ password)
     */
    @GetMapping("/users")
    @Operation(
            summary = "Lister tous les utilisateurs",
            description = "Retourne tous les comptes utilisateurs avec leurs points. " +
                    "Le mot de passe n'est jamais exposé dans la réponse (DTO)."
    )
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs")
    public ResponseEntity<List<UserDTO>> users() {
        List<UserDTO> dtos = userService.getAll()
                .stream()
                .map(UserDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // =========================================================
    //  GET /api/admin/users/{id}
    // =========================================================

    /**
     * Retourne le profil complet d'un utilisateur spécifique.
     *
     * Réponse 200 : UserDTO avec id, nom, prenom, email, points, role
     * Réponse 500 : utilisateur introuvable
     */
    @GetMapping("/users/{id}")
    @Operation(
            summary = "Détail d'un utilisateur",
            description = "Retourne le profil complet d'un utilisateur sans son mot de passe."
    )
    @ApiResponse(responseCode = "200", description = "Profil de l'utilisateur")
    public ResponseEntity<UserDTO> user(
            @Parameter(description = "ID de l'utilisateur", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(UserDTO.from(userService.getById(id)));
    }

    // =========================================================
    //  GET /api/admin/deposits
    // =========================================================

    /**
     * Retourne tous les dépôts de plastique effectués sur le système.
     * Utilisé pour les rapports et l'analyse des données.
     *
     * FIX : retourne List<DepositDTO> au lieu de List<Deposit>
     * pour éviter la boucle infinie JSON.
     *
     * Réponse 200 : liste de tous les dépôts avec userId, binId, poids, points, date
     */
    @GetMapping("/deposits")
    @Operation(
            summary = "Tous les dépôts de plastique",
            description = "Liste complète de tous les dépôts effectués sur toutes les bornes. " +
                    "Utilisé pour générer les rapports et analyser les données de collecte."
    )
    @ApiResponse(responseCode = "200", description = "Liste de tous les dépôts")
    public ResponseEntity<List<DepositDTO>> deposits() {
        List<DepositDTO> dtos = depositService.getAllDeposits()
                .stream()
                .map(DepositDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
