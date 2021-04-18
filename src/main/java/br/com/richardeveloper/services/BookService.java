package br.com.richardeveloper.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.richardeveloper.models.Book;

public interface BookService {

	public Book save(Book book);

	public Optional<Book> findById(Long id);

	public void delete(Book book);

	public Book update(Book book);

	public Page<Book> findByAuthorAndTitle(Book book, Pageable pageable);

	public Optional<Book> findByIsbn(String isbn);


}
