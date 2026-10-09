package com.springSecurityImpl.learnSpringSecurity.advice;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private Boolean status;
    private T data;
    private ApiError error;
}
