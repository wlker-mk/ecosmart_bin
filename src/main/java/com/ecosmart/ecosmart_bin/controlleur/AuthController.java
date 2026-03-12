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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "🔐 Authentification", description = "Inscription et connexion (email ou carte étudiante)")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Inscription (citoyen ou étudiant)",
            description = "Si 'carteEtudiante' fourni → compte étudiant + 10 pts bonus. Points x2 par dépôt.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscription réussie"),
            @ApiResponse(responseCode = "400", description = "Email ou carte déjà utilisés")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "Citoyen", value =
                    "{ \"nom\": \"Dupont\", \"prenom\": \"Jean\", \"email\": \"jean@gmail.com\", " +
                            "\"password\": \"pass123\", \"telephone\": \"+228 90000000\" }"),
            @ExampleObject(name = "Étudiant", value =
                    "{ \"nom\": \"Koffi\", \"prenom\": \"Amavi\", \"email\": \"amavi@univ.tg\", " +
                            "\"password\": \"pass123\", \"telephone\": \"+228 91000000\", " +
                            "\"carteEtudiante\": \"ETU-2024-00123\" }")
    }))
    // ✅ BUG CORRIGÉ — retourne Map<String,Object> au lieu de ResponseEntity<?>
    // L'ancien code avec <?> + .orElse() ne compilait pas
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            Map<String, Object> response = new HashMap<>();
            // ✅ AJOUT — message différent selon le type de compte
            response.put("message", user.isEstEtudiant()
                    ? "Inscription réussie ! Bienvenue étudiant, +10 points bonus offerts 🎓"
                    : "Inscription réussie !");
            response.put("userId", user.getId());
            response.put("nom", user.getNom());
            response.put("email", user.getEmail());
            response.put("points", user.getPoints());
            response.put("estEtudiant", user.isEstEtudiant()); // ✅ AJOUT
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> erreur = new HashMap<>();
            erreur.put("erreur", e.getMessage());
            return ResponseEntity.badRequest().body(erreur);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion par email OU par carte étudiante",
            description = "Si carteEtudiante fourni → priorité sur email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "Par email", value =
                    "{ \"email\": \"jean@gmail.com\", \"password\": \"pass123\" }"),
            @ExampleObject(name = "Par carte étudiante", value =      // ✅ AJOUT
                    "{ \"carteEtudiante\": \"ETU-2024-00123\", \"password\": \"pass123\" }")
    }))
    // ✅ BUG CORRIGÉ — utilise if/else au lieu de .map().orElse()
    // .map().orElse() avec ResponseEntity<?> = erreur de compilation garantie
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Optional<User> optUser;

        // ✅ AJOUT — détection automatique du mode de connexion
        if (request.getCarteEtudiante() != null && !request.getCarteEtudiante().isBlank()) {
            optUser = userService.loginParCarte(request.getCarteEtudiante(), request.getPassword());
        } else {
            optUser = userService.loginParEmail(request.getEmail(), request.getPassword());
        }

        if (optUser.isPresent()) {
            User user = optUser.get();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Connexion réussie !");
            response.put("userId", user.getId());
            response.put("nom", user.getNom());
            response.put("prenom", user.getPrenom());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("points", user.getPoints());
            response.put("estEtudiant", user.isEstEtudiant());       // ✅ AJOUT
            response.put("carteEtudiante", user.getCarteEtudiante()); // ✅ AJOUT
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> erreur = new HashMap<>();
            erreur.put("erreur", "Identifiants incorrects. Vérifiez votre email/carte et mot de passe.");
            return ResponseEntity.status(401).body(erreur);
        }
    }
}