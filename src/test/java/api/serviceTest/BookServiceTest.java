package api.serviceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
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

import api.exception.BusinessException;
import api.model.entity.Book;
import api.model.repository.BookRepository;
import api.service.BookService;
import api.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService service;
	@MockBean
	BookRepository repository;

	@BeforeEach
	public void setup() {
		this.service = new BookServiceImpl(repository);
	}

	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {

		//cenario
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when( repository.save(book)).thenReturn
		(Book.builder().id(1l)
				.isbn("123")
				.author("Fulano")
				.title("As aventuras").build());

		//execução
		Book savedBook = service.save(book);

		//verificação
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("123");
		assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
		assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
	}

	@Test
	@DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
	public void sholdNotSaveABookWithDuplicatedISBN() {

		//cenario
		Book book = createValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);


		// execução
		Throwable exception = Assertions.catchThrowable(() -> service.save(book)); // cathThrowable tem como parametro expres Lambda que chama quem lança exceção 


		// verificações 
		assertThat(exception)
		.isInstanceOf(BusinessException.class)
		.hasMessage("Isbn já cadastrado.");

		Mockito.verify(repository, Mockito.never()).save(book);


	}

	@Test
	@DisplayName("Deve obter um livro por ID")
	public void getByIdTest() {
		Long id = 1L;

		Book book = createValidBook();
		book.setId(id);
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));


		//execução
		Optional<Book> foundBook = service.getById(id);

		assertThat( foundBook.isPresent() ).isTrue();
		assertThat( foundBook.get().getId() ).isEqualTo(id);
		assertThat( foundBook.get().getAuthor() ).isEqualTo(book.getAuthor());
		assertThat( foundBook.get().getIsbn() ).isEqualTo(book.getIsbn());
		assertThat( foundBook.get().getTitle() ).isEqualTo(book.getTitle());
	}

	@Test
	@DisplayName("Deve retornar vazio ao obter um livro por ID inexistente na base")
	public void getByIdNotExistentTest() {
		Long id = 1L;

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());


		//execução
		Optional<Book> book = service.getById(id);

		assertThat( book.isPresent() ).isFalse();

	}

	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() {
		// Cenário
		Long id = 1L;
		Book book = createValidBook();
		book.setId(id);


		// execução
		assertDoesNotThrow(() -> service.delete(book));
		Mockito.verify(repository, Mockito.times(1)).delete(book);


	}

	@Test
	@DisplayName("Deve lançar exceção IllegalArgumentException ao tentar deletar um livro que não existe")
	public void deleteInexistentBookTest() {
		Book book = new Book();
		assertThrows(IllegalArgumentException.class, () -> service.delete(book));
		Mockito.verify(repository, Mockito.never() ).delete(book);
	}

	@Test
	@DisplayName("Deve atualizar um livro")
	public void updateBookTest() {
		// Cenário
		Long id = 1L;
		Book updatingBook = createValidBook();
		updatingBook.setId(id);
		
		Book updatedBook = Book.builder().id(id).author("Lucas").title("Direito Civil").isbn("123").build();
		Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

		// execução
		
		Book book = service.update(updatingBook);
		
		//verificações
		assertThat( book.getId() ).isEqualTo( updatedBook.getId() );
		assertThat( book.getTitle() ).isEqualTo( updatedBook.getTitle() );
		assertThat( book.getAuthor() ).isEqualTo( updatedBook.getAuthor() );
		assertThat( book.getIsbn() ).isEqualTo( updatedBook.getIsbn() );
	}


	@Test
	@DisplayName("Deve lançar exceção IllegalArgumentException ao tentar atualizar um livro que não existe")
	public void updateInexistentBookTest() {
		Book book = new Book();
		assertThrows(IllegalArgumentException.class, () -> service.update(book));
		Mockito.verify(repository, Mockito.never() ).save(book);
	}


	@SuppressWarnings("unchecked")
	@Test 
	@DisplayName("Deve filtrar livros pelos atributos")
	public void findBookTest() {
		
		Book book = createValidBook();
		PageRequest pageRequest = PageRequest.of(0, 10);
		List<Book> lista = Arrays.asList(book);
		
		
		Page<Book> page = new PageImpl<Book>( lista , pageRequest, 1);
		
		Mockito.when( repository.findAll(Mockito.any(Example.class),Mockito.any(PageRequest.class))).thenReturn(page);
		
		Page<Book> result = service.find(book, pageRequest);
		
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(lista);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		
	}

	private Book createValidBook() {
		return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
	}

}
