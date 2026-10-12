package com.springSecurityImpl.learnSpringSecurity.repository;

import com.springSecurityImpl.learnSpringSecurity.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    Optional<Session> findByUserIdAndTokenAndIsRevokedFalse(UUID userId, String token);

    List<Session> findByUserIdAndIsRevokedFalse(UUID userId);
}