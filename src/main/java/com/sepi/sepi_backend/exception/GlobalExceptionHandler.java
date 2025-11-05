package com.sepi.sepi_backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe para centralizar o tratamento de exceções na API.
 */
@ControllerAdvice
public class GlobalExceptionHandler
{
	
	/**
	 * Trata erros de validação de DTOs (@Valid).
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex)
	{
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
				errors.put(error.getField(), error.getDefaultMessage()));
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Trata exceções de violação de integridade de dados (ex: email duplicado).
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex)
	{
		return new ResponseEntity<>("Erro de integridade de dados: " + ex.getMessage(), HttpStatus.CONFLICT);
	}
	
	/**
	 * Trata exceções de recurso não encontrado (ex: Localidade inexistente).
	 */
	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<String> handleResourceNotFound(RecursoNaoEncontradoException ex)
	{
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	/**
	 * Exceção genérica de tempo de execução.
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex)
	{
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
}
