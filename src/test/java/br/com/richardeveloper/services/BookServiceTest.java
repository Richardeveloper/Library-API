package br.com.richardeveloper.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
	
	@Test
	@DisplayName("Deve obert um livro por Id")
	public void getByIdTest() {
		
		Long id = 1L;
		Book book = Book.builder().id(id).author("James Jhonson").title("As Aventuras de James Jhonson").isbn("85412369").build();
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
		
		Optional<Book> foundBook = service.findById(id);
		
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId().equals(book.getId()));
		assertThat(foundBook.get().getAuthor().equals(book.getAuthor()));
		assertThat(foundBook.get().getTitle().equals(book.getTitle()));
		assertThat(foundBook.get().getIsbn().equals(book.getIsbn()));
	}
	
	@Test
	@DisplayName("Deve retornar vazio ao obter um livro inexistente")
	public void bookNotFoundTest() {
		
		Long id = 1L;
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Book> foundBook = service.findById(id);
		
		assertThat(foundBook.isPresent()).isFalse();
		
	}
	
	@Test
	@DisplayName("Deve deletar um livro com sucesso")
	public void deleteBookTest() {
		
		Book book = Book.builder().id(1L).author("James Jhonson").title("As Aventuras de James Jhonson").isbn("85412369").build();
		
		Assertions.assertDoesNotThrow(() -> repository.delete(book));
		
		Mockito.verify(repository, Mockito.times(1)).delete(book);
		
	}

	@Test
	@DisplayName("Deve lançar execeção caso livro seja nulo ou id seja nulo")
	public void deleteInexistBookTest() {
		
		Book book = new Book();
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));
		
		Mockito.verify(repository, Mockito.never()).delete(book);
	}
	
	@Test
	@DisplayName("Deve atualizar um livro com sucesso")
	public void updateBookTest() {
		
		Long id = 1L;
		Book book = Book.builder().id(id).author("James Jhonson").title("As Aventuras de James Jhonson").isbn("85412369").build();
		Book updatedBook = Book.builder().id(id).author("James Jhonson").title("As Aventuras de James Jhonson").isbn("85412369").build();
		
		Mockito.when(repository.save(book)).thenReturn(updatedBook);
		
		Book update = service.update(book);
		
		assertThat(update.getId()).isEqualTo(updatedBook.getId());
		assertThat(update.getTitle()).isEqualTo(updatedBook.getTitle());
		assertThat(update.getAuthor()).isEqualTo(updatedBook.getAuthor());
		assertThat(update.getIsbn()).isEqualTo(updatedBook.getIsbn());
	}
	
	@Test
	@DisplayName("Deve lançar execeção caso livro seja nulo ou id seja nulo")
	public void updateInexistBookTest() {
		
		Book book = new Book();
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));
		
		Mockito.verify(repository, Mockito.never()).delete(book);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Deve filtar livros pelas propriedades")
	public void findBookTest() {
		
		Book book = Book.builder().id(1L).author("James Jhonson").title("As Aventuras de James Jhonson").isbn("85412369").build();		
		List<Book> books = new ArrayList<Book>();
		books.add(book);
		
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		Page<Book> page = new PageImpl<Book>(books, pageRequest, 1);
		
		Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))) 
				.thenReturn(page);
		
		Page<Book> result = service.findByAuthorAndTitle(book, pageRequest);
		
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(books);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
	}
	
	
}
