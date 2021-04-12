package br.com.richardeveloper.resources.exceptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

public class ApiErrors {
	
	private List<String> errors;

	public ApiErrors(BindingResult bindingResult) {
		this.errors = new ArrayList<String>();
		bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
	}
	
	public ApiErrors(BusinessException e) {
		this.errors = Arrays.asList(e.getMessage());
	}
	
	public List<String> getErrors() {
		return errors;
	}
}
