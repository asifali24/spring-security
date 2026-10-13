package com.springSecurityImpl.learnSpringSecurity.repository;

import com.springSecurityImpl.learnSpringSecurity.entities.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostsRepository extends JpaRepository<Posts, UUID> {
}