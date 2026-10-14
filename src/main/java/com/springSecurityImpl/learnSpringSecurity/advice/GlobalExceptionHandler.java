package com.springSecurityImpl.learnSpringSecurity.advice;


import com.springSecurityImpl.learnSpringSecurity.exceptions.InvalidSessionException;
import com.springSecurityImpl.learnSpringSecurity.exceptions.ResourceNotFoundException;
import com.springSecurityImpl.learnSpringSecurity.exceptions.ResourcesAlreadyExist;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> resourcesNotFoundException(ResourceNotFoundException exception){
        ApiError err = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(formatError(err));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> validationErrors(
            MethodArgumentNotValidException exception
    ) {
        List<String> errors = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .toList();

        ApiError err = ApiError.builder()
                .message("Invalid input validation "+ exception.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .errorList(errors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(formatError(err));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> internalServerError(Exception exc){
        log.error("cause ->",exc);
        ApiError err = ApiError
                .builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Internal Server Error "+ exc.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(formatError(err));
    }


    @ExceptionHandler(ResourcesAlreadyExist.class)
    public ResponseEntity<ApiResponse<?>> resourceAlreadyExists(
            ResourcesAlreadyExist exception) {

        ApiError err = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(formatError(err));
    }


    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> jwtException(JwtException e){
        ApiError err = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(formatError(err));
    }


    @ExceptionHandler(InvalidSessionException.class)
    public ResponseEntity<ApiResponse<?>> invalidSessionException(
            InvalidSessionException exception) {

        ApiError err = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(formatError(err));
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> authenticationException(
            AuthenticationException exception) {

        ApiError error = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("Authentication is required")
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(formatError(error));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> accessDeniedException(
            AccessDeniedException exception) {

        ApiError error = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message("You do not have permission to access this resource")
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(formatError(error));
    }

    private ApiResponse<?> formatError(ApiError err){
        return ApiResponse.<Object>builder()
                .error(err)
                .status(false)
                .build();
    }
}
