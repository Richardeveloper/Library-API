package br.com.richardeveloper.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.richardeveloper.models.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private BookRepository repository;
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando existir livro com isbn já existente")
	void returnTrueWhenIsbnExists() {
		
		String isbn = "1029384756";
		Book book = Book.builder().author("James Jhonson").title("As Aventuras de James Jhonson").isbn(isbn).build();
		entityManager.persist(book);
		
		boolean existsByIsbn = repository.existsByIsbn(isbn);
		
		assertThat(existsByIsbn).isTrue();
		
	}
	
	@Test
	@DisplayName("Deve retornar falso quando existir não livro com isbn já existente")
	void returnFalsoWhenDoesntIsbnExists() {
		
		String isbn = "1029384756";

		boolean existsByIsbn = repository.existsByIsbn(isbn);
		
		assertThat(existsByIsbn).isFalse();
		
	}
	
	@Test
	@DisplayName("Deve retornar um livro pelo Id")
	public void findByIdTest() {
		
		Book book = Book.builder().author("James Jhonson").title("As Aventuras de James Jhonson").isbn("1029384756").build();
		entityManager.persist(book);
		
		Optional<Book> foundBook = repository.findById(book.getId());
		
		assertThat(foundBook.isPresent()).isTrue();
		
	}
	
	@Test
	@DisplayName("Deve salvar um livro com sucesso")
	public void saveBookTest() {
		
		Book book = Book.builder().author("James Jhonson").title("As Aventuras de James Jhonson").isbn("1029384756").build();
		
		Book savedBook = repository.save(book);
				
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
		assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
		assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
	}
	
	@Test
	@DisplayName("Deve deletar um livro com sucesso")
	public void deleteBookTest() {
		
		Book book = Book.builder().author("James Jhonson").title("As Aventuras de James Jhonson").isbn("1029384756").build();
		entityManager.persist(book);
		
		Book foundBook = entityManager.find(Book.class, book.getId());
		
		repository.delete(foundBook);
		
		Book deletedBook = entityManager.find(Book.class, book.getId());
		
		assertThat(deletedBook).isNull(); 
	}

	
}
