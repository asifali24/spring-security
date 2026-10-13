package com.springSecurityImpl.learnSpringSecurity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Builder
public class PostDto {
    private String header;
    private String body;
}
