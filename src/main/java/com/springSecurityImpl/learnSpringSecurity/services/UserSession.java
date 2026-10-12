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
    public void generateNewSessionAndInvalidatePrevOnce(UUID userId, String token){

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

        Session toBeCreated = Session.builder()
                .userId(userId)
                .token(token)
                .build();
         sessionRepository.save(toBeCreated);
    }

    public Boolean isSessionValid(UUID userId,String token){

        Session isValidSession = sessionRepository.findByUserIdAndTokenAndIsRevokedFalse(userId,token).orElse(null);


        return isValidSession != null;
    }
}
