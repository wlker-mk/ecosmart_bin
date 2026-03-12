package com.ecosmart.ecosmart_bin.controlleur;

import com.ecosmart.ecosmart_bin.dto.LoginRequest;
import com.ecosmart.ecosmart_bin.dto.RegisterRequest;
import com.ecosmart.ecosmart_bin.entity.User;
import com.ecosmart.ecosmart_bin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================
 *  AuthController — Gestion de l'authentification
 * ============================================================
 *
 *  BASE URL : /api/auth
 *
 *  Endpoints :
 *  ┌──────────────────────────────────────────────────────────┐
 *  │  POST  /api/auth/register  → Inscription utilisateur     │
 *  │  POST  /api/auth/login     → Connexion utilisateur       │
 *  └──────────────────────────────────────────────────────────┘
 *
 *  ⚠️  DÉMO : mots de passe en clair.
 *  En production → BCryptPasswordEncoder + JWT.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "🔐 Authentification", description = "Inscription et connexion des utilisateurs")
public class AuthController {

    private final UserService userService;

    // =========================================================
    //  POST /api/auth/register
    // =========================================================

    /**
     * Inscrit un nouvel utilisateur.
     *
     * Corps attendu :
     * {
     *   "nom": "Dupont", "prenom": "Jean",
     *   "email": "jean@example.com",
     *   "password": "motdepasse123",
     *   "telephone": "+228 90000000"
     * }
     *
     * Réponse 200 : { "message", "userId", "nom", "email" }
     * Réponse 400 : { "erreur": "Email déjà utilisé" }
     */
    @PostMapping("/register")
    @Operation(
            summary = "Inscription d'un nouvel utilisateur",
            description = "Crée un compte avec le rôle USER et 0 points. " +
                    "Erreur 400 si l'email existe déjà."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscription réussie"),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value =
                    "{ \"nom\": \"Dupont\", \"prenom\": \"Jean\", " +
                            "\"email\": \"jean@example.com\", \"password\": \"motdepasse123\", " +
                            "\"telephone\": \"+228 90000000\" }"))
    )
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Inscription réussie");
            response.put("userId", user.getId());
            response.put("nom", user.getNom());
            response.put("email", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> erreur = new HashMap<>();
            erreur.put("erreur", e.getMessage());
            return ResponseEntity.badRequest().body(erreur);
        }
    }

    // =========================================================
    //  POST /api/auth/login
    // =========================================================

    /**
     * Connecte un utilisateur existant.
     *
     * Corps attendu :
     * { "email": "jean@example.com", "password": "motdepasse123" }
     *
     * Réponse 200 : { "message", "userId", "nom", "role", "points" }
     * Réponse 401 : { "erreur": "Email ou mot de passe incorrect" }
     *
     * FIX : utilise if/else au lieu de .map().orElse() pour éviter
     * l'erreur de compilation avec ResponseEntity<?> générique.
     */
    @PostMapping("/login")
    @Operation(
            summary = "Connexion d'un utilisateur",
            description = "Vérifie l'email et le mot de passe. " +
                    "Retourne le rôle et les points de l'utilisateur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value =
                    "{ \"email\": \"jean@example.com\", \"password\": \"motdepasse123\" }"))
    )
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Optional<User> optUser = userService.login(request.getEmail(), request.getPassword());

        if (optUser.isPresent()) {
            User user = optUser.get();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Connexion réussie");
            response.put("userId", user.getId());
            response.put("nom", user.getNom());
            response.put("role", user.getRole());
            response.put("points", user.getPoints());
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> erreur = new HashMap<>();
            erreur.put("erreur", "Email ou mot de passe incorrect");
            return ResponseEntity.status(401).body(erreur);
        }
    }
}
