package com.springSecurityImpl.learnSpringSecurity.services;


import com.springSecurityImpl.learnSpringSecurity.dto.CreatePostDto;
import com.springSecurityImpl.learnSpringSecurity.dto.PostDto;
import com.springSecurityImpl.learnSpringSecurity.entities.Posts;
import com.springSecurityImpl.learnSpringSecurity.entities.User;
import com.springSecurityImpl.learnSpringSecurity.exceptions.ResourceNotFoundException;
import com.springSecurityImpl.learnSpringSecurity.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServ {
    
    private final PostsRepository postsRepository;
    private final ModelMapper modelMapper;


    @Transactional
//    @Cacheable(cacheNames = "post-", key = "#result.id")
    @CachePut(cacheNames = "posts", key = "#result.id")
    public PostDto createPost(CreatePostDto post) {

        Posts newPost = modelMapper.map(post,Posts.class);
//        User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal() ;

//        newPost.setUser(user);
        Posts createdPost = postsRepository.save(newPost);

        return modelMapper.map(createdPost,PostDto.class);
    }

    public List<PostDto> getAllPost() {
        List<Posts> allPost = postsRepository.findAll();

        return allPost.stream()
                .map(p -> modelMapper.map(p,PostDto.class))
                .toList();
    }

    @Cacheable(cacheNames = "posts", key = "#id")
    public PostDto getPostById(UUID id) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found with id "+id));
        return modelMapper.map(post,PostDto.class);
    }

    @Transactional
    @CachePut(cacheNames = "posts", key = "#id")
    public PostDto updatePostById(UUID id, PostDto oldPost) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found with id "+id));
        post.setBody(oldPost.getBody());
        post.setHeader(oldPost.getHeader());
        return modelMapper.map(post,PostDto.class);

    }

    @CachePut(cacheNames = "posts", key = "#id")
    public void deletePostById(UUID id) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found with id "+id));

        postsRepository.delete(post);
        return;
    }
}
