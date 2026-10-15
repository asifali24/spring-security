package com.springSecurityImpl.learnSpringSecurity.controller;


import com.springSecurityImpl.learnSpringSecurity.dto.CreatePostDto;
import com.springSecurityImpl.learnSpringSecurity.dto.PostDto;
import com.springSecurityImpl.learnSpringSecurity.services.PostServ;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class Post {

    private final PostServ postServ;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody CreatePostDto post){
        log.info("Controller post -> {}",post);
        return ResponseEntity.status(HttpStatus.CREATED).body(postServ.createPost(post));
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPost(){
        return ResponseEntity.status(HttpStatus.OK).body(postServ.getAllPost());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(postServ.getPostById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePostById(@PathVariable UUID id, @RequestBody PostDto post){
        return ResponseEntity.status(HttpStatus.OK).body(postServ.updatePostById(id,post));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<PostDto> DeletePostById(@PathVariable UUID id){
        postServ.deletePostById(id);
        return ResponseEntity.noContent().build();
    }
}
