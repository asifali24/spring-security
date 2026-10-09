package com.springSecurityImpl.learnSpringSecurity.advice;


import com.springSecurityImpl.learnSpringSecurity.exceptions.ResourceNotFoundException;
import com.springSecurityImpl.learnSpringSecurity.exceptions.ResourcesAlreadyExist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
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
                .status(HttpStatus.CONFLICT)
                .body(formatError(err));
    }


    private ApiResponse<?> formatError(ApiError err){
        return ApiResponse.<Object>builder()
                .error(err)
                .status(false)
                .build();
    }
}
