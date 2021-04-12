package br.com.richardeveloper.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
@WebMvcTest
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
	
}
