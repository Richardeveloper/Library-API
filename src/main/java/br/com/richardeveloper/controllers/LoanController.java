package br.com.richardeveloper.controllers;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.richardeveloper.models.Book;
import br.com.richardeveloper.models.Loan;
import br.com.richardeveloper.models.dto.LoanDTO;
import br.com.richardeveloper.services.BookService;
import br.com.richardeveloper.services.LoanService;

@RestController
@RequestMapping(value = "api/loans")
public class LoanController {

	private LoanService loanService;

	private BookService bookService;
	
	private ModelMapper modelMapper;
	
	@Autowired	
	public LoanController(LoanService loanService, BookService bookService, ModelMapper modelMapper) {
		this.loanService = loanService;
		this.bookService = bookService;
		this.modelMapper = modelMapper;
	}
	
	@PostMapping
	public ResponseEntity<LoanDTO> save(@RequestBody LoanDTO dto){
		Book book = bookService.findByIsbn(dto.getIsbn()).get();
		Loan entity = Loan.builder().book(book).customer(dto.getCustomer()).loanDate(LocalDate.now()).build();
		entity = loanService.save(entity);
		return new ResponseEntity<LoanDTO>(modelMapper.map(entity, LoanDTO.class), HttpStatus.CREATED);
	}
	
}
