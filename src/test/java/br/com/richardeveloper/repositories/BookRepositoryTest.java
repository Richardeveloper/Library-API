package br.com.richardeveloper.repositories;

import static org.assertj.core.api.Assertions.assertThat;

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

}
