package com.springSecurityImpl.learnSpringSecurity.controller;


import com.springSecurityImpl.learnSpringSecurity.dto.PostDto;
import com.springSecurityImpl.learnSpringSecurity.services.PostServ;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class Post {

    private final PostServ postServ;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto post){
        log.info("Controller post -> {}",post);
        return ResponseEntity.status(HttpStatus.CREATED).body(postServ.createPost(post));
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPost(){
        return ResponseEntity.status(HttpStatus.OK).body(postServ.getAllPost());
    }
}
