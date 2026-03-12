package com.ecosmart.ecosmart_bin.service;

import com.ecosmart.ecosmart_bin.dto.RegisterRequest;
import com.ecosmart.ecosmart_bin.entity.User;
import com.ecosmart.ecosmart_bin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ✅ AJOUT — constante pour le bonus étudiant
    private static final int BONUS_POINTS_ETUDIANT = 10;

    // ─────────────────────────────────────────────
    //  INSCRIPTION
    // ─────────────────────────────────────────────

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + request.getEmail());
        }

        String carte = request.getCarteEtudiante();
        boolean estEtudiant = false;
        int pointsDepart = 0;

        // ✅ AJOUT — si carte fournie : vérification unicité + bonus 10 pts
        if (carte != null && !carte.isBlank()) {
            if (userRepository.existsByCarteEtudiante(carte)) {
                throw new RuntimeException("Carte étudiante déjà utilisée : " + carte);
            }
            estEtudiant = true;
            pointsDepart = BONUS_POINTS_ETUDIANT;
        }

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(request.getPassword())
                .telephone(request.getTelephone())
                .carteEtudiante(carte)    // ✅ AJOUT
                .estEtudiant(estEtudiant) // ✅ AJOUT
                .points(pointsDepart)     // ✅ AJOUT — 0 ou 10 selon profil
                .role("USER")
                .build();

        return userRepository.save(user);
    }

    // ─────────────────────────────────────────────
    //  CONNEXION
    // ─────────────────────────────────────────────

    // ✅ BUG CORRIGÉ — avant : une seule méthode login(email, password)
    // Maintenant : deux méthodes séparées selon le mode

    // Mode 1 : email (tout le monde)
    public Optional<User> loginParEmail(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password));
    }

    // Mode 2 : carte étudiante ✅ AJOUT
    public Optional<User> loginParCarte(String carteEtudiante, String password) {
        return userRepository.findByCarteEtudiante(carteEtudiante)
                .filter(u -> u.getPassword().equals(password));
    }

    // ─────────────────────────────────────────────
    //  CRUD
    // ─────────────────────────────────────────────

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + id));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    // ─────────────────────────────────────────────
    //  POINTS
    // ─────────────────────────────────────────────

    // ✅ AJOUT — si étudiant → points x2 automatiquement
    public void addPoints(User user, int points) {
        int pointsFinaux = user.isEstEtudiant() ? points * 2 : points;
        user.setPoints(user.getPoints() + pointsFinaux);
        userRepository.save(user);
    }

    // ✅ BUG CORRIGÉ — message d'erreur plus clair avec les montants
    public void deductPoints(User user, int points) {
        if (user.getPoints() < points) {
            throw new RuntimeException(
                    "Points insuffisants. Vous avez " + user.getPoints() + " pts, il faut " + points + " pts."
            );
        }
        user.setPoints(user.getPoints() - points);
        userRepository.save(user);
    }
}