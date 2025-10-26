package com.sepi.sepi_backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.sasl.AuthenticationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        log.error("Recurso não encontrado: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetails> handleBadRequestException(
            BadRequestException ex, HttpServletRequest request) {

        log.error("Requisição inválida: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDetails> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        log.error("Erro de validação: {}", ex.getMessage());

        Map<String, List<String>> validationErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            validationErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                "Dados de entrada inválidos",
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDetails> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {

        log.error("Erro de autenticação: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "Não Autorizado",
                "Credenciais inválidas ou token expirado",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {

        log.error("Credenciais inválidas: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.UNAUTHORIZED.value(),
                "Credenciais Inválidas",
                "Email ou senha incorretos",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetails);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        log.error("Acesso negado: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.FORBIDDEN.value(),
                "Acesso Negado",
                "Você não tem permissão para acessar este recurso",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDetails);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        log.error("Violação de integridade de dados: {}", ex.getMessage());

        String message = "Erro de integridade de dados";
        if (ex.getMessage().contains("Duplicate entry")) {
            message = "Dados duplicados - registro já existe";
        } else if (ex.getMessage().contains("foreign key constraint")) {
            message = "Não é possível excluir - existem registros relacionados";
        }

        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados",
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.error("Argumento inválido: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Argumento Inválido",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Erro interno do servidor: ", ex);

        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
}
