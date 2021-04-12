package br.com.richardeveloper.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.richardeveloper.resources.exceptions.ApiErrors;
import br.com.richardeveloper.resources.exceptions.BusinessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrors> handleValidationException(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		
		return new ResponseEntity<ApiErrors>(new ApiErrors(bindingResult), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiErrors> handleValidationException(BusinessException e) {
		return new ResponseEntity<ApiErrors>(new ApiErrors(e), HttpStatus.BAD_REQUEST);
	}
	
}
