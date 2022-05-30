package api.controllerTest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.dto.LoanDTO;
import api.exception.BusinessException;
import api.model.entity.Book;
import api.model.entity.Loan;
import api.resource.LoanController;
import api.service.BookService;
import api.service.LoanService;


@ExtendWith(SpringExtension.class) 
@ActiveProfiles("test")
@WebMvcTest(controllers =  LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

	static final String LOAN_API = "/api/loans";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	private BookService bookService;
	
	@MockBean
	private LoanService loanService;
	
	@Test
	@DisplayName("Deve realizar um emprestimo")
	public void creatLoan() throws Exception{
		
		LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().isbn("123").id(1L).build();
		
		BDDMockito.given( bookService.getBookByIsbn("123") )
				.willReturn(  Optional.of( book ) );
		
		Loan loan =  Loan.builder().id(1L).customer("Fulano").book(book).build();
		
		BDDMockito.given( loanService.save(Mockito.any(Loan.class)) ).willReturn(loan);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post( LOAN_API )
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform( request )
			.andExpect( status().isCreated() )
			.andExpect( content().string("1"));
			
	}	
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer emprestimo de livro inexistente")
	public void invalidIsbnCreateBookTest() throws Exception{
		
		LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given( bookService.getBookByIsbn("123") )
				.willReturn(  Optional.empty()  );
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post( LOAN_API )
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform( request )
			.andExpect( status().isBadRequest() )
			.andExpect( jsonPath("errors", Matchers.hasSize(1)) )
			.andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
		
	}
	
	@Test
	@DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro já está emprestado")
	public void loanedBookOnCreateLoanTest() throws Exception{
		
		LoanDTO dto = LoanDTO.builder().isbn("123").customer("Fulano").build();
		String json = new ObjectMapper().writeValueAsString(dto);
	
		Book book = Book.builder().isbn("123").id(1L).build();
		
		BDDMockito.given( bookService.getBookByIsbn("123") )
		.willReturn(  Optional.of( book ) );
		
		BDDMockito.given( loanService.save(Mockito.any(Loan.class)) )
			.willThrow( new BusinessException("Book alredy loaned"));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post( LOAN_API )
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform( request )
			.andExpect( status().isBadRequest() )
			.andExpect( jsonPath("errors", Matchers.hasSize(1)) )
			.andExpect(jsonPath("errors[0]").value("Book alredy loaned"));
		
	}
}
