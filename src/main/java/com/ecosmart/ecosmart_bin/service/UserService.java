// UserService.java
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

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + request.getEmail());
        }
        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(request.getPassword())
                .telephone(request.getTelephone())
                .points(0)
                .role("USER")
                .build();
        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getPassword().equals(password));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + id));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void addPoints(User user, int points) {
        user.setPoints(user.getPoints() + points);
        userRepository.save(user);
    }

    public void deductPoints(User user, int points) {
        if (user.getPoints() < points) {
            throw new RuntimeException("Points insuffisants");
        }
        user.setPoints(user.getPoints() - points);
        userRepository.save(user);
    }
}