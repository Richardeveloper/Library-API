package br.com.richardeveloper.controllers;

import java.time.LocalDate;
import java.util.Optional;

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
import br.com.richardeveloper.models.Loan;
import br.com.richardeveloper.models.dto.LoanDTO;
import br.com.richardeveloper.services.BookService;
import br.com.richardeveloper.services.LoanService;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

	private static final String LOAN_API = "/api/loans";
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private BookService bookService;

	@MockBean
	private LoanService loanService;
	
	@Test
	@DisplayName("Deve realizar um emprestimo")
	public void createLoanTest() throws Exception {
		
		LoanDTO dto = LoanDTO.builder().isbn("159736482").customer("Stive").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1L).isbn("159736482").build();
		BDDMockito.given(bookService.findByIsbn(dto.getIsbn())).willReturn(Optional.of(book));
		
		Loan loan = Loan.builder().id(1L).customer("Stive").book(book).loanDate(LocalDate.now()).build();
		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(1L));
	}
}
