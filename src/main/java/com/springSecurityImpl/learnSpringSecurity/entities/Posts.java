package com.springSecurityImpl.learnSpringSecurity.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Posts extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String header;

    private String body;

    @ManyToOne
    private User user;
}
