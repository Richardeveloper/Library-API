package br.com.richardeveloper.controllers;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
	
	@GetMapping("/{id}")
	public ResponseEntity<BookDTO> findById(@PathVariable Long id) {
		Book book = service.findById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return new ResponseEntity<BookDTO>(modelMapper.map(book, BookDTO.class), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
		Book book = service.findById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.delete(book);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<BookDTO> update(@PathVariable Long id, @RequestBody BookDTO dto) {
		return service.findById(id)
				.map(book -> { 
					book.setAuthor(dto.getAuthor());
					book.setTitle(dto.getAuthor());
					book = service.update(book);
					return new ResponseEntity<BookDTO>(modelMapper.map(book, BookDTO.class), HttpStatus.OK);
					}
				)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping
	public ResponseEntity<Page<BookDTO>> findByAuthorAndTitle(BookDTO dto, Pageable pageable){
		Book entity = modelMapper.map(dto, Book.class);
		Page<Book> result = service.findByAuthorAndTitle(entity, pageable);
//		List<BookDTO> list = result.getContent().stream().map(book -> modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
//		Page<BookDTO> resultDTO = new PageImpl<BookDTO>(list, pageable, result.getTotalElements());
		Page<BookDTO> resultDTO = result.map(book -> modelMapper.map(book, BookDTO.class));
		return new ResponseEntity<Page<BookDTO>>(resultDTO, HttpStatus.OK);
	}
	
}
