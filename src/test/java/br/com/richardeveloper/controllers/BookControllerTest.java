package br.com.richardeveloper.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.richardeveloper.models.Book;
import br.com.richardeveloper.models.dto.BookDTO;
import br.com.richardeveloper.resources.exceptions.BusinessException;
import br.com.richardeveloper.services.BookService;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	BookService service;
	
	@Test
	@DisplayName("Deve criar um livro com sucesso")
	public void createBookTest() throws Exception {
		
		BookDTO dto = BookDTO.builder().author("James Jhonson").title("Aventuras de Jhonson").isbn("123456789").build();
		Book savedBook = Book.builder().id(1L).author("James Jhonson").title("Aventuras de Jhonson").isbn("123456789").build();
		
		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
					.post(BOOK_API)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id", notNullValue()))
			.andExpect(MockMvcResultMatchers.jsonPath("title", is(dto.getTitle())))
			.andExpect(MockMvcResultMatchers.jsonPath("author", is(dto.getAuthor())))
			.andExpect(MockMvcResultMatchers.jsonPath("isbn", is(dto.getIsbn())));
			
	}
	
	@Test
	@DisplayName("Deve lançar exceção quando não houver dados suficientes para criar livro")
	public void createInvalidBookTest() throws Exception {
		
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(3)));
	}
	
	@Test
	@DisplayName("Deve lançar exceção ao tentar cadastrar livro com ISBN repetido")
	public void createBookWithDuplicatedIsbn() throws Exception {

		BookDTO dto = BookDTO.builder().author("James Jhonson").title("Aventuras de Jhonson").isbn("123456789").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		String errorMsg = "Ibsn já cadastrado.";
		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(errorMsg));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value(errorMsg));
	}
	
	@Test
	@DisplayName("Deve buscar informações de um livro")
	public void getBookTest() throws Exception {
		
		Long id = 1L;
		Book book = Book.builder().id(id).author("James Jhonson").title("Aventuras de Jhonson").isbn("123456789").build();
		
		BDDMockito.given(service.findById(id)).willReturn(Optional.of(book));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/"+id))
				.accept(MediaType.APPLICATION_JSON);		
	
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id", is(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("title", is(book.getTitle())))
			.andExpect(MockMvcResultMatchers.jsonPath("author", is(book.getAuthor())))
			.andExpect(MockMvcResultMatchers.jsonPath("isbn", is(book.getIsbn())));
	}
	
	@Test
	@DisplayName("Deve lançar execeção quando livro não existir")
	public void bookNotFoundTest() throws Exception {
		
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() throws Exception {
		
		Book book = Book.builder().id(1L).build();
		
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.of(book));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	@DisplayName("Deve lançar exceção quando não encontrar um livro para deletar")
	public void deleteInexistBookTest() throws Exception {
		
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve atualizar os dados de um livro com sucesso")
	public void updateBookTest() throws Exception {
		
		Long id = 1L;
		Book book = Book.builder().id(id).author("James Jhonson").title("Aventuras de Jhonson").isbn("123456789").build();
		String json = new ObjectMapper().writeValueAsString(book);
		
		Book updatingBook = Book.builder().id(id).author("Washington").title("Title").isbn("753914268").build();
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.of(updatingBook));
		
		BDDMockito.given(service.update(updatingBook)).willReturn(book);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id", is(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("title", is(book.getTitle())))
			.andExpect(MockMvcResultMatchers.jsonPath("author", is(book.getAuthor())))
			.andExpect(MockMvcResultMatchers.jsonPath("isbn", is(book.getIsbn())));
		
	}
	
	@Test
	@DisplayName("Deve lançar exceção ao tentar atualizar um livro inexistente")
	public void updateInexistBookTest() throws Exception {
		
		Book book = Book.builder().author("James Jhonson").title("Aventuras de Jhonson").isbn("123456789").build();
		String json = new ObjectMapper().writeValueAsString(book);
		
		BDDMockito.given(service.findById(Mockito.anyLong()))
			.willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(BOOK_API.concat("/" + 1))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve filtar livros")
	public void findBooksTest() throws Exception {
		
		Long id = 1L;
		Book book = Book.builder().id(id).author("James Jhonson").title("Aventuras de Jhonson").isbn("123456789").build();
		List<Book> books = new ArrayList<Book>();
		books.add(book);
		
		BDDMockito.given(service.findByAuthorAndTitle(Mockito.any(Book.class), Mockito.any(Pageable.class)))
			.willReturn(new PageImpl<Book>(books, PageRequest.of(0 , 10), 1));
		
		String queryString = String.format(BOOK_API + "?title=%s&author=%s&page=0&size=10",book.getTitle(),book.getAuthor());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(queryString)
				.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
			.andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
			.andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(10))
			.andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
	}
	
}
