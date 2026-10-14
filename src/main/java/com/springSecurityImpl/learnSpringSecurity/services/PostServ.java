package com.springSecurityImpl.learnSpringSecurity.services;


import com.springSecurityImpl.learnSpringSecurity.dto.PostDto;
import com.springSecurityImpl.learnSpringSecurity.entities.Posts;
import com.springSecurityImpl.learnSpringSecurity.entities.User;
import com.springSecurityImpl.learnSpringSecurity.repository.PostsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServ {
    
    private final PostsRepository postsRepository;
    private final ModelMapper modelMapper;


    @Transactional
    public PostDto createPost(PostDto post) {
        log.info("Post ->{}",post);
        Posts newPost = modelMapper.map(post,Posts.class);
        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal() ;

        newPost.setUser(user);
        log.info("newPost -> {}",newPost);
        Posts createdPost = postsRepository.save(newPost);
        log.info("created post -> {}",createdPost);
        return modelMapper.map(createdPost,PostDto.class);
    }

    public List<PostDto> getAllPost() {
        List<Posts> allPost = postsRepository.findAll();

        return allPost.stream()
                .map(p -> modelMapper.map(p,PostDto.class))
                .toList();
    }
}
