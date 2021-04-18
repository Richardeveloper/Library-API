package br.com.richardeveloper.services;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import br.com.richardeveloper.models.Book;
import br.com.richardeveloper.repositories.BookRepository;
import br.com.richardeveloper.resources.exceptions.BusinessException;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<Book> findById(Long id) {
		return this.repository.findById(id);
	}
		
	@Override
	public Book save(Book book) {
		validation(book);
		return this.repository.save(book);
	}

	@Override
	public void delete(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("Book id cant be null");
		}
		this.repository.delete(book);
	}

	@Override
	public Book update(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("Book id cant be null");
		}
		return this.repository.save(book);
	}

	public void validation(Book book) {
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado.");
		}
		if(repository.existsByTitle(book.getTitle())) {
			throw new BusinessException("Título já cadastrado.");
		}
	}

	@Override
	public Page<Book> findByAuthorAndTitle(Book book, Pageable pageable) {
		Example<Book> example = Example.of(book, 
				ExampleMatcher.matching()
							  .withIgnoreCase()
							  .withIgnoreNullValues()
							  .withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example, pageable);
	}

	@Override
	public Optional<Book> findByIsbn(String isbn) {
		// TODO Auto-generated method stub
		return null;
	}


}
