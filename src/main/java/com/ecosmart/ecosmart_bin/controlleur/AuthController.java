package com.ecosmart.ecosmart_bin.controlleur;

import com.ecosmart.ecosmart_bin.entity.User;
import com.ecosmart.ecosmart_bin.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     * Inscription utilisateur
     */
    @PostMapping("/register")
    public User register(@RequestBody User user){
        user.setRole("USER");
        user.setPoints(0);
        return userRepository.save(user);
    }

    /**
     * Connexion
     */
    @PostMapping("/login")
    public String login(@RequestBody User request){
        return userRepository.findByEmail(request.getEmail())
                .filter(u -> u.getPassword().equals(request.getPassword()))
                .map(u -> "Connexion réussie : " + u.getRole())
                .orElse("Email ou mot de passe incorrect");
    }
}