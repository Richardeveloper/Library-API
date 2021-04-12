package br.com.richardeveloper.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.richardeveloper.models.Book;
import br.com.richardeveloper.repositories.BookRepository;
import br.com.richardeveloper.resources.exceptions.BusinessException;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

	private BookService service;
	
	@MockBean
	private BookRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository);
	}
	
	@Test
	@DisplayName("Deve salvar livro com sucesso")
	void saveBookTest() {

		Book book = Book.builder().author("James Jhonson").title("As Aventuras de James Jhonson").isbn("78954321").build();
		
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book))
			.thenReturn(Book.builder().id(1L).author("James Jhonson").title("As Aventuras de James Jhonson").isbn("78954321").build());
		
		Book savedBook = service.save(book);
		
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getAuthor()).isEqualTo("James Jhonson");
		assertThat(savedBook.getTitle()).isEqualTo("As Aventuras de James Jhonson");
		assertThat(savedBook.getIsbn()).isEqualTo("78954321");
		
	}
	
	@Test
	@DisplayName("Deve lançar exceção ao tentar cadastrar livro com ISBN repetido")
	public void throwExceptionWhenIsbnExists() {
		
		Book book = Book.builder().id(1L).author("James Jhonson").title("As Aventuras de James Jhonson").isbn("78954321").build();
		
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		Throwable exception = catchThrowable(() -> service.save(book));
		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado.");
				
		Mockito.verify(repository, Mockito.never()).save(book);
		
	}

	
	@Test
	@DisplayName("Deve lançar exceção ao tentar cadastrar livro com título repetido")
	public void throwExceptionWhenTitutloExists() {
		
		Book book = Book.builder().author("James Jhonson").title("As Aventuras de James Jhonson").isbn("85412369").build();
		
		Mockito.when(repository.existsByTitle(book.getTitle())).thenReturn(true);
		
		Throwable catchThrowable = catchThrowable(() -> service.save(book));
		assertThat(catchThrowable).isInstanceOf(BusinessException.class).hasMessage("Título já cadastrado.");
		
		Mockito.verify(repository, Mockito.never()).save(book);
	}

}
