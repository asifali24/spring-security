package com.springSecurityImpl.learnSpringSecurity.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_session")
@Builder
public class Session extends Auditing {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "User ID is required")
    @Column(nullable = false)
    private UUID userId;

    @NotBlank(message = "Token is required")
    @Column(nullable = false)
    private String token;

    @Builder.Default
    @NotNull(message = "Revoked status is required")
    @Column(nullable = false)
    private Boolean isRevoked = false;

}