package com.app.controllerTest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.app.dto.BookDTO;
import com.app.exception.BusinessException;
import com.app.model.entity.Book;
import com.app.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class) 
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";

	@Autowired
	MockMvc mvc;

	@MockBean // utilizado para mockar e manipular o comportamento do service
	BookService service;

	@Test
	@DisplayName("Deve criar um livro com sucesso.")
	public void createBookTest() throws Exception {

		BookDTO dto = createNewBookDTO();
		Book savedBook = Book.builder().id(10L).author("Author").title("Meu Livro").isbn("1233212").build();

		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook); // simula o servico de salvar um book e retorna um book como se fosse da entidade

		String json = new ObjectMapper().writeValueAsString(dto); 

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders //MOCK DA REQUEST
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);

		mvc // EXECUÇÃO DA REQUEST MOCKADA COM O QUE SE ESPERA DA RESPOSTA DO TESTE (i.e: um livro criado com sucesso)
		.perform(request)
		.andExpect( status().isCreated() )
		.andExpect( jsonPath("id").isNotEmpty() )
		.andExpect( jsonPath("id").value(10L) )
		.andExpect( jsonPath("title").value(dto.getTitle()) )
		.andExpect( jsonPath("author").value(dto.getAuthor()) )
		.andExpect( jsonPath("isbn").value(dto.getIsbn()) )
		;

	}

	@Test
	@DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro.")
	public void createInvalidBookTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(new BookDTO());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);

		mvc.perform(request)
		.andExpect(status().isBadRequest())
		.andExpect( jsonPath("errors", hasSize(3)));

	}

	@Test
	@DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn duplicado.")
	public void createBookWithDuplicatedIsbn() throws Exception {

		BookDTO dto =  createNewBookDTO();		
		String json = new ObjectMapper().writeValueAsString(dto);
		String mensagemErro= "Isbn já cadastrado.";


		BDDMockito.given(service.save(Mockito.any(Book.class)))
		.willThrow(new BusinessException(mensagemErro));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);

		mvc.perform(request)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errors", hasSize(1)))
		.andExpect(jsonPath("errors[0]").value(mensagemErro));
	}

	@Test
	@DisplayName("Deve obter informações de um livro.")
	public void getById() throws Exception {

		// cenario (given)
		Long id =1L;
		Book book = Book.builder()
				.id(id)
				.author(createNewBookDTO().getAuthor())
				.title(createNewBookDTO().getTitle())
				.isbn(createNewBookDTO().getIsbn())
				.build();
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));


		// execução (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/"+id))
				.contentType(MediaType.APPLICATION_JSON);

		// verificação 
		mvc.perform(request)
		.andExpect(status().isOk())
		.andExpect( jsonPath("id").isNotEmpty() )
		.andExpect( jsonPath("id").value(id) )
		.andExpect( jsonPath("title").value(createNewBookDTO().getTitle()) )
		.andExpect( jsonPath("author").value(createNewBookDTO().getAuthor()) )
		.andExpect( jsonPath("isbn").value(createNewBookDTO().getIsbn()) );


	}

	@Test
	@DisplayName("Deve lançar exceção (resource not found) se livro não existir.")
	public void bookNotFoundTest() throws Exception {

		// cenario (given)
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		// execução (when)
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/"+ 1))
				.contentType(MediaType.APPLICATION_JSON);
		
		// verificação 
				mvc.perform(request)
				.andExpect(status().isNotFound())
				;
		
		
		
	}


	public BookDTO createNewBookDTO() {
		return BookDTO.builder().author("Author").title("Meu Livro").isbn("1233212").build();
	}



}
