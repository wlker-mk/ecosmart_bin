// RewardUser.java
package com.ecosmart.ecosmart_bin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reward_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    private LocalDateTime dateEchange;
    private String statut; // EN_ATTENTE, VALIDE, REFUSE
}