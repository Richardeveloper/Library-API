package br.com.richardeveloper.controllers;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.richardeveloper.models.Book;
import br.com.richardeveloper.models.dto.BookDTO;
import br.com.richardeveloper.services.BookService;

@RestController
@RequestMapping(value = "/api/books")
public class BookController {

	private BookService service;

	private ModelMapper modelMapper;

	public BookController(BookService service, ModelMapper modelMapper) {
		this.service = service;
		this.modelMapper = modelMapper;
	}

	@PostMapping
	public ResponseEntity<BookDTO> save(@RequestBody @Valid BookDTO dto) {
		Book entity = modelMapper.map(dto, Book.class);
		entity = service.save(entity);
		return new ResponseEntity<BookDTO>(modelMapper.map(entity, BookDTO.class), HttpStatus.CREATED);
	}

}
