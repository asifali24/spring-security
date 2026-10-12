package com.springSecurityImpl.learnSpringSecurity.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_session")
@Builder
public class Session {


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

    @CreatedDate
    @NotNull(message = "Created at is required")
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @NotNull(message = "Updated at is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}