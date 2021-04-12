package br.com.richardeveloper.services;

import java.util.List;

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
	
	public List<Book> findAll() {
		return repository.findAll();
	}
	
	public Book save(Book book) {
		validation(book);
		return repository.save(book);
	}

	public void validation(Book book) {
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado.");
		}
		if(repository.existsByTitle(book.getTitle())) {
			throw new BusinessException("Título já cadastrado.");
		}
	}

}
