package com.springSecurityImpl.learnSpringSecurity.services;


import com.springSecurityImpl.learnSpringSecurity.entities.Session;
import com.springSecurityImpl.learnSpringSecurity.repository.SessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserSession {

    private final SessionRepository sessionRepository;


    @Transactional
    public void generateNewSessionAndInvalidatePrevOnce(UUID userId, String accessToken, String refreshToken){

        List<Session> prevNonLogoutSession = sessionRepository.findByUserIdAndIsRevokedFalse(userId);

        if(!prevNonLogoutSession.isEmpty()){

        prevNonLogoutSession
                .stream()
                .map((session) -> {
                    session.setIsRevoked(true);
                    return session;
                })
                .toList();
        }

        Session accessSession = Session.builder()
                .userId(userId)
                .token(accessToken)
                .build();
        sessionRepository.save(accessSession);

//        Session refreshSession = Session.builder()
//                .userId(userId)
//                .token(refreshToken)
//                .build();
//        sessionRepository.save(accessSession);
    }

    public Boolean isSessionValid(UUID userId,String token){

        Session isValidSession = sessionRepository.findByUserIdAndTokenAndIsRevokedFalse(userId,token).orElse(null);


        return isValidSession != null;
    }

    @Transactional
    public void revokeSession(UUID userId, String token) {
        sessionRepository.findByUserIdAndTokenAndIsRevokedFalse(userId, token)
                .ifPresent(session -> session.setIsRevoked(true));
    }
}
